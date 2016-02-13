#start ES
echo "Starting Elastic search"
/usr/local/apps/elasticsearch-1.7.2/bin/elasticsearch &

#start glassfish
echo "Starting glassfish"
# verbose causes the process to remain in the foreground so that docker can track it
asadmin start-domain --verbose