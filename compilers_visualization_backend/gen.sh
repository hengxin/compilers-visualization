mkdir -p src/main/generated && protoc --java_out='src/main/generated'  src/main/resources/protobuf/*.proto
