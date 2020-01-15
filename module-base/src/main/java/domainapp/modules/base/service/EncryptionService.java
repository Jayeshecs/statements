/**
 * 
 */
package domainapp.modules.base.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.config.ConfigurationService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author jayeshecs
 * Ref: https://stackoverflow.com/questions/24338108/java-encrypt-string-with-existing-public-key-file
 */
@DomainService(
		nature = NatureOfService.DOMAIN,
		objectType = "base.EncryptionService"
)
public class EncryptionService {

	private static final String ALGO_RSA = "RSA";
	private PublicKey publicKey;
	private PrivateKey privateKey;

	public EncryptionService() {
		// DO NOTHING
	}
	
	@PostConstruct
	public void init() {
		String keyLocation = configurationService.getProperty("base.kl", System.getProperty("user.home") + "/.ssh");
		File keyLocationFile = new File(keyLocation);
		if (!keyLocationFile.exists()) {
			throw new IllegalArgumentException("Location '" + keyLocation + "' does not exists");
		}
		if (!keyLocationFile.isDirectory()) {
			throw new IllegalArgumentException("Location '" + keyLocation + "' must be a valid directory");
		}
		if (!keyLocationFile.canWrite() || !keyLocationFile.canExecute() || !keyLocationFile.canRead()) {
			throw new IllegalArgumentException("Location '" + keyLocation + "' must have read/write/execute permission");
		}
		File file = new File(keyLocationFile, ".dmbse"); // key file name
		
		if (!file.exists()) {
			KeyPair keyPair = generateKeyPair();
			saveGeneratedKeys(file, keyPair);
		}
		
		List<String> encodedKeys = loadEncodedKeys(file);
		publicKey = loadPublicKeyFromEncodedKeys(encodedKeys);
		privateKey = loadPrivateKeyFromEncodedKeys(encodedKeys);
	}

	@Programmatic
	private PublicKey loadPublicKeyFromEncodedKeys(List<String> encodedKeys) {
		try {
			/* Generate public key. */
			X509EncodedKeySpec ks = new X509EncodedKeySpec(Base64.getDecoder().decode(encodedKeys.get(0).getBytes()));
			KeyFactory kf = KeyFactory.getInstance(ALGO_RSA);
			PublicKey pub = kf.generatePublic(ks);
			return pub;
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new IllegalStateException("Error occurred while loading public key from encoded keys", e);
		}
	}
	
	@Programmatic
	private PrivateKey loadPrivateKeyFromEncodedKeys(List<String> encodedKeys) {
		try {
			/* Generate private key. */
			PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(encodedKeys.get(1).getBytes()));
			KeyFactory kf = KeyFactory.getInstance(ALGO_RSA);
			PrivateKey privateKey = kf.generatePrivate(ks);
			return privateKey;
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new IllegalStateException("Error occurred while loading private key from encoded keys", e);
		}
	}

	@Programmatic
	private List<String> loadEncodedKeys(File file) {
		try {
			byte[] bytes = Files.readAllBytes(file.toPath());
			byte[] decodedBytes = Base64.getDecoder().decode(bytes);
			Gson gson = new GsonBuilder().create();
			@SuppressWarnings("unchecked")
			List<String> encodedKeys = gson.fromJson(new String(decodedBytes), List.class);
			return encodedKeys;
		} catch (IOException e) {
			throw new IllegalStateException("Error occurred while loading encoded keys from file - " + file);
		}
	}

	/**
	 * @param file
	 * @param publicKey
	 * @param privateKey
	 */
	@Programmatic
	private void saveGeneratedKeys(File file, KeyPair keyPair) {
		// encode key pair
		String encodedKeyPair = encodeKeyPair(keyPair);
		// save encodedKeyPair
		saveEncodedKeyPair(file, encodedKeyPair);
	}

	/**
	 * @param keyPair
	 * @return
	 */
	@Programmatic
	private String encodeKeyPair(KeyPair keyPair) {
		PublicKey publicKey = keyPair.getPublic();
		PrivateKey privateKey = keyPair.getPrivate();
		// get encoded keys
		String encodedPublicKey = new String(Base64.getEncoder().encode(publicKey.getEncoded()));
		String encodedPrivateKey = new String(Base64.getEncoder().encode(privateKey.getEncoded()));
		// prepare list of encoded keys
		List<String> keys = new ArrayList<String>();
		keys.add(encodedPublicKey); 
		keys.add(encodedPrivateKey);
		// convert keys list to JSON
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(keys);
		// decode json string using Base64
		String encodedKeyPair = Base64.getEncoder().encodeToString(json.getBytes());
		return encodedKeyPair;
	}

	/**
	 * @param file
	 * @param encodedKeyPair
	 */
	@Programmatic
	private void saveEncodedKeyPair(File file, String encodedKeyPair) {
		try {
			Files.write(file.toPath(), encodedKeyPair.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
		} catch (IOException e) {
			throw new IllegalStateException("Error occurred while saving generated key pair");
		}
	}

	/**
	 * @return
	 */
	@Programmatic
	private KeyPair generateKeyPair() {
		try {
			KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance(ALGO_RSA);
			keyGenerator.initialize(2048);
			KeyPair keyPair = keyGenerator.generateKeyPair();
			return keyPair;
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("Error occurred while generating key pair");
		}
	}
	
	@PreDestroy
	public void destroy() {
		privateKey = null;
		publicKey = null;
	}
	
	@Programmatic
	public String encrypt(String content) {
		try {
			Cipher instance = Cipher.getInstance(ALGO_RSA);
			instance.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] encrypted = instance.doFinal(content.getBytes());
			byte[] encoded = Base64.getEncoder().encode(encrypted);
			return new String(encoded);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
			throw new IllegalStateException("Error occurred while encrypting given content using public key", e);
		}
	}
	
	public String decrypt(String encryptedContent) {
		try {
			Cipher instance = Cipher.getInstance(ALGO_RSA);
			instance.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] encryptedBytes = Base64.getDecoder().decode(encryptedContent.getBytes());
			byte[] decrypted = instance.doFinal(encryptedBytes);
			return new String(decrypted);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
			throw new IllegalStateException("Error occurred while decrypting given content using private key", e);
		}
	}
	
	@Inject
	protected ConfigurationService configurationService;
}
