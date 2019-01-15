
import org.scalacheck.Prop.{BooleanOperators, forAll}
import org.scalacheck.{Gen, Properties}

object SecureStoreTest extends Properties("KeyStore") {

  private val path = getClass.getResource("/keystore").getPath

  private val storePassword = "randompass"

  private val entries = Gen.alphaStr

  property("read and write entry") = forAll(entries, entries) { (alias: String, pwd: String) =>
    val store = SecureStore.fromFile(path, storePassword)

    (alias.length > 0 && pwd.length > 0) ==> {
      store.addEntry(alias, pwd)

      store.readPasswordForEntry(alias, storePassword) == pwd
    }
  }
}
