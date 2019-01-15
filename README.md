# skeystore
A small Java Key Store Wrapper in Scala

They Key Store Wrapper supports adding aliases to a `JCEKS` keystore. 

We can create a keystore using the following command.

```
keytool -keystore keystore -genkey -alias nico -storetype jceks     
```
Notice that not all types of keystores will support the required functionality. 

The following test shows how our library is used. 

```
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
```
`private val storePassword = "randompass"` is the keystore password we used when creating the keystore. Since this test is 
written using `ScalaCheck`, it will actually run `100` iterations with different combinations of `alias` and `pwd` and it
makes sure we can successfully read and write from the keystore. 

## Cross Building

Use the following command to build.

```
sbt +assembly
```

This command build against `scala 2.11.8` so it can be used on `Apache Spark` and `scala 2.12.8`. It will generate 2 `.jar` 
outputs, one for each version.
