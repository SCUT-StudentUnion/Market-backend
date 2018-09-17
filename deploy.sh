#!/bin/bash

remoteTarget="/var/market/backend/"
server="travis@139.199.6.12"
serviceName="market-backend-staging.service"
backendDir="target"
finalJarName="market-staging.jar"

jarFile="$(find "$backendDir" -name "*.jar")"
if [[ ! $jarFile ]]
then
    echo jar file not found.
    exit 1
fi

if [[ ! -f $jarFile ]]
then
    echo can not determine jar file.
    echo $jarFile
    exit 1
fi

echo Add ssh deploy key
echo '|1|9udVfwBeyZcomCse5eDJMrYpJaU=|8yBJ80C4uk3PNO7exU2iU3pwAA0= ecdsa-sha2-nistp256 AAAAE2VjZHNhLXNoYTItbmlzdHAyNTYAAAAIbmlzdHAyNTYAAABBBHW9Oph8rgxN6bsdeduwyyLqmGvl6/FtowOzlUw8UV5t15bj2RrQ7CCjxkDE3Z+WvyW+zIYXL02XgIpxvWG7ySg=' >> $HOME/.ssh/known_hosts
eval `ssh-agent`
openssl aes-256-cbc -K $encrypted_5f6c449f24c3_key -iv $encrypted_5f6c449f24c3_iv -in market-travis-deploy.enc -out market-travis-deploy -d
chmod 600 market-travis-deploy
ssh-add market-travis-deploy

echo Updating $server:$remoteTarget

echo Stopping service
ssh $server "systemctl --user stop $serviceName" || { echo Cannot stop. Abort.; exit 1; }

echo Transferring files
rsync -zv --checksum "$jarFile" "$server:$remoteTarget" || exit 1;

echo restarting service
jarFileName="$(basename "$jarFile")"
ssh $server \
"cd '$remoteTarget';
rm -f '$finalJarName';
ln -s '$jarFileName' '$finalJarName';
systemctl --user start $serviceName;" || { echo Cannot start. Abort.; exit 1; }

echo Published to remote successfully.
