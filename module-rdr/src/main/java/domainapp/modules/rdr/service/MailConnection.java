/**
 * 
 */
package domainapp.modules.rdr.service;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.AndTerm;
import javax.mail.search.FromStringTerm;
import javax.mail.search.OrTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import domainapp.modules.rdr.dom.MailConnectionProfile;

/**
 * @author jayeshecs
 *
 */
public class MailConnection {
	
	public static interface MailConnectionStatsMBean {
		
		void connectionTime(long timeInMillis);
		
		Long getAverageConnectionTime();
		
		void folderOpenTime(long timeInMillis);
		
		Long getAverageFolderOpenTime();

		void searchTime(long timeInMillis);
		
		Long getAverageSearchTime();
	}
	
	public static class MailConnectionStats implements MailConnectionStatsMBean {

		private List<Long> connectionTime = new ArrayList<Long>();

		private List<Long> folderOpenTime = new ArrayList<Long>();

		private List<Long> searchTime = new ArrayList<Long>();
		
		public void connectionTime(long timeInMillis) {
			connectionTime.add(timeInMillis);
		}

		public void folderOpenTime(long timeInMillis) {
			folderOpenTime.add(timeInMillis);
		}

		public void searchTime(long timeInMillis) {
			searchTime.add(timeInMillis);
		}

		public Long getAverageConnectionTime() {
			if (connectionTime.isEmpty()) {
				return -1l;
			}
			long totalTime = 0l;
			for (long time : connectionTime) {
				totalTime += time;
			}
			return totalTime / connectionTime.size();
		}

		public Long getAverageFolderOpenTime() {
			if (folderOpenTime.isEmpty()) {
				return -1l;
			}
			long totalTime = 0l;
			for (long time : folderOpenTime) {
				totalTime += time;
			}
			return totalTime / folderOpenTime.size();
		}

		public Long getAverageSearchTime() {
			if (searchTime.isEmpty()) {
				return -1l;
			}
			long totalTime = 0l;
			for (long time : searchTime) {
				totalTime += time;
			}
			return totalTime / searchTime.size();
		}
		
	}
	
	static MailConnectionStatsMBean stats = new MailConnectionStats();
	
	static {
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		ObjectName objectName = null;
		try {
		    objectName = new ObjectName("outil.rnd.mail.imap:type=MailConnectionStats");
		    server.registerMBean(stats, objectName);
		} catch (MalformedObjectNameException e) {
		    e.printStackTrace();
		} catch (InstanceAlreadyExistsException e) {
			e.printStackTrace();
		} catch (MBeanRegistrationException e) {
			e.printStackTrace();
		} catch (NotCompliantMBeanException e) {
			e.printStackTrace();
		}
	}

	private MailConnectionProfile configuration;
	
	private List<Store> connectedStores = new ArrayList<>(); 
	
	private List<Folder> openedFolder = new ArrayList<>(); 

	public MailConnection(MailConnectionProfile configuration) {
		this.configuration = configuration;
	}

	public Store connectStore() {
		String protocol = "imap";
		if (configuration.getSecure()) {
			protocol = protocol + "s";
		}
		Properties properties = new Properties();
		properties.setProperty("mail." + protocol + ".host", configuration.getHostname());
		properties.setProperty("mail." + protocol + ".port", configuration.getPort());
		properties.setProperty("mail." + protocol + ".starttls.enable", String.valueOf(configuration.getStarttls()));

		// Setup authentication, get session
		Session emailSession = Session.getInstance(properties, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(configuration.getUsername(), configuration.getPassword());
			}
		});
		emailSession.setDebug(configuration.getDebug());

		long start = System.currentTimeMillis();
		try {
			Store store = emailSession.getStore(protocol);
			store.connect();
			connectedStores.add(store);
			return store;
		} catch (NoSuchProviderException e) {
			throw new IllegalStateException("Failed to connect - " + e.getMessage(), e);
		} catch (MessagingException e) {
			throw new IllegalStateException("Failed to connect - " + e.getMessage(), e);
		} finally {
			stats.connectionTime(System.currentTimeMillis() - start);
		}
	}
	
	public Folder getFolder(Store store, String folderName) {
		long start = System.currentTimeMillis();
		try {
			Folder folder = store.getFolder(folderName);
			folder.open(Folder.READ_ONLY);
			openedFolder.add(folder);
			return folder;
		} catch (MessagingException e) {
			throw new IllegalStateException("Failed to get folder - " + e.getMessage(), e);
		} finally {
			stats.folderOpenTime(System.currentTimeMillis() - start);
		}
	}
	
	public void closeFolder(Folder folder) {
		if (folder != null && folder.isOpen()) {
			try {
				folder.close(false);
				openedFolder.remove(folder);
			} catch (MessagingException e) {
				// DO NOTHING
			}
		}
	}
	
	public void closeStore(Store store) {
		if (store != null && store.isConnected()) {
			try {
				store.close();
				connectedStores.remove(store);
			} catch (MessagingException e) {
				// DO NOTHING
			}
		}
	}
	
	public void closeAll() {
		for (Folder folder : openedFolder.toArray(new Folder[] {})) {
			closeFolder(folder);
		}
		openedFolder.clear();
		for (Store store : connectedStores.toArray(new Store[] {})) {
			closeStore(store);
		}
		connectedStores.clear();
	}
	
	public Message[] search(String folderName, String subjectWords, String sender, Boolean hasAttachment) {
		if (folderName == null) {
			folderName = "INBOX";
		}
		Store store = null;
		Folder folder = null;
		try {
			store = connectStore();
			folder = getFolder(store, folderName);
			List<SearchTerm> criteria = new ArrayList<SearchTerm>();
			if (subjectWords != null) {
				List<SearchTerm> subjectWordTerms = new ArrayList<SearchTerm>();
				for (String word : subjectWords.split(" |,")) {
					if (word.trim().isEmpty()) { // skip whitespace
						continue ;
					}
					subjectWordTerms.add(new SubjectTerm(word));
				}
				if (!subjectWordTerms.isEmpty()) {
					criteria.add(new AndTerm(subjectWordTerms.toArray(new SearchTerm[] {})));
				}
			}
			if (sender != null) {
				List<SearchTerm> senderTerms = new ArrayList<SearchTerm>();
				for (String word : sender.split(",")) {
					senderTerms.add(new FromStringTerm(word.trim()));
				}
				if (!senderTerms.isEmpty()) {
					criteria.add(new OrTerm(senderTerms.toArray(new SearchTerm[] {})));
				}
			}
			long start = System.currentTimeMillis();
			Message[] messages = folder.search(new AndTerm(criteria.toArray(new SearchTerm[] {})));
			stats.searchTime(System.currentTimeMillis() - start);
			return messages;
		} catch (MessagingException e) {
			throw new IllegalStateException("Fail to access mailbox - " + e.getMessage(), e);
		}
	}

	/**
	 * @param message
	 * @throws MessagingException
	 */
	private void messageHeaders(Message message) throws MessagingException {
		Enumeration<Header> headers = message.getAllHeaders();
		while (headers.hasMoreElements()) {
			Header header = headers.nextElement();
			System.out.println(header.getName() + " = " + header.getValue());
		}
	}

	public void dumpStats() {
		System.out.println("========================================================================");
		System.out.println("Average connection time (ms): " + stats.getAverageConnectionTime());
		System.out.println("Average folder open time (ms): " + stats.getAverageFolderOpenTime());
		System.out.println("Average search time (ms): " + stats.getAverageSearchTime());
		System.out.println("========================================================================");
	}
}
