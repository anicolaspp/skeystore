import java.io.{FileInputStream, FileOutputStream}
import java.security.KeyStore

import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec


sealed trait SecureStore {
  def readPasswordForEntry(alias: String, password: String): String

  def addEntry(entry: String, entryPassword: String): Unit
}

object SecureStore {
  def fromFile(path: String, password: String): SecureStore = FileKeyStore(path, password)

  lazy val keyStoreType = "JCEKS"
  lazy val secretKeyFactoryType = "PBE"

  private case class FileKeyStore(path: String, keyStorePassword: String) extends SecureStore {

    def addEntry(entry: String, entryPassword: String): Unit = addEntryToKeyStore(entry, entryPassword)

    override def readPasswordForEntry(alias: String, password: String): String = getPasswordFromKeystore(alias)

    private def getPasswordFromKeystore(entry: String) = {
      val ks = KeyStore.getInstance(keyStoreType)
      ks.load(null, keyStorePassword.toCharArray)
      val keyStorePP = new KeyStore.PasswordProtection(keyStorePassword.toCharArray)
      val fIn = new FileInputStream(path)
      ks.load(fIn, keyStorePassword.toCharArray)
      val factory = SecretKeyFactory.getInstance(secretKeyFactoryType)
      val ske = ks.getEntry(entry, keyStorePP).asInstanceOf[KeyStore.SecretKeyEntry]
      val keySpec = factory.getKeySpec(ske.getSecretKey, classOf[PBEKeySpec]).asInstanceOf[PBEKeySpec]
      val password = keySpec.getPassword
      new String(password)
    }

    private def addEntryToKeyStore(entry: String, entryPassword: String) = {
      val factory = SecretKeyFactory.getInstance(secretKeyFactoryType)
      val generatedSecret = factory.generateSecret(new PBEKeySpec(entryPassword.toCharArray))
      val ks = KeyStore.getInstance(keyStoreType)
      ks.load(null, keyStorePassword.toCharArray)
      val keyStorePP = new KeyStore.PasswordProtection(keyStorePassword.toCharArray)
      ks.setEntry(entry, new KeyStore.SecretKeyEntry(generatedSecret), keyStorePP)
      val fos = new FileOutputStream(path)
      ks.store(fos, keyStorePassword.toCharArray)
    }
  }
}
