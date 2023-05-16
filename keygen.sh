# generacija zasebnih ključev.
echo "Generating private keys..."
keytool -genkey -alias catprivate -keystore cat.private -storetype PKCS12 -keyalg rsa -dname "CN=cat" -storepass catpwd1 -keypass catpwd1 -validity 365 && \
keytool -genkey -alias dogprivate -keystore dog.private -storetype PKCS12 -keyalg rsa -dname "CN=dog" -storepass dogpwd -keypass dogpwd -validity 365 && \
keytool -genkey -alias birdprivate -keystore bird.private -storetype PKCS12 -keyalg rsa -dname "CN=bird" -storepass birdpwd -keypass birdpwd -validity 365 && \
keytool -genkey -alias serverprivate -keystore server.private -storetype PKCS12 -keyalg rsa -dname "CN=localhost" -storepass serverpwd -keypass serverpwd -validity 365 && \

# generacija javnih ključev
echo "Generating public keys..."
keytool -export -alias catprivate -keystore cat.private -file temp.key -storepass catpwd1 && \
keytool -import -noprompt -alias catpublic -keystore client.public -file temp.key -storepass public && \
rm temp.key && \
keytool -export -alias dogprivate -keystore dog.private -file temp.key -storepass dogpwd && \
keytool -import -noprompt -alias dogpublic -keystore client.public -file temp.key -storepass public && \
rm temp.key && \
keytool -export -alias birdprivate -keystore bird.private -file temp.key -storepass birdpwd && \
keytool -import -noprompt -alias birdpublic -keystore client.public -file temp.key -storepass public && \
rm temp.key && \

# javni ključ za server
keytool -export -alias serverprivate -keystore server.private -file temp.key -storepass serverpwd && \
keytool -import -noprompt -alias serverpublic -keystore server.public -file temp.key -storepass public && \
rm temp.key && \
echo "Key generation complete."