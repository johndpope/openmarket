# install rqlite
go get github.com/otoolep/rqlite
export GOPATH=$PWD

# Startup 2 nodes
$GOPATH/bin/rqlite node_1 &
sleep 2s
$GOPATH/bin/rqlite -join localhost:4001 -p 4002 node_2 &
sleep 2s
# $GOPATH/bin/rqlite -join localhost:4001 -p 4003 node_3 &
# sleep 2s

# Now kill node 2 and clear it, assuming it has an error
ps aux | grep -ie node_2 | awk '{print $2}' | xargs kill -9
rm -rf node_2

# Now try to rejoin node 1
$GOPATH/bin/rqlite -join localhost:4001 -p 4002 node_2 &

# And you'll see that the error is that it stalls on attempting to join leader
#. The only way to get it working again is to delete node 1 completely!

sleep 5s

#kills all the rqlite instances
ps aux | grep -ie rqlite | awk '{print $2}' | xargs kill -9
rm -rf node_1 node_2 node_3