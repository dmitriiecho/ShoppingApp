package krio.systemdesign.shoppingapp.core.config

object ServerConfig {
    // HTTP is allowed for this address in apps/shop/src/main/res/xml/network_security_config.xml, and the server
    // builds image addresses from it (PUBLIC_URL in server/deploy.sh). Update all three places if it changes.
    const val BASE_URL = "http://2.56.204.151:8080/"
}
