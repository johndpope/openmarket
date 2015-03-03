s.add("cd ~/.openmarket");
s.add("mkdir db ");
s.add("cd db/");
s.add("export GOPATH=$PWD");
s.add("go get github.com/otoolep/rqlite");
s.add("$GOPATH/bin/rqlite data");