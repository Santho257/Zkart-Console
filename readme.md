It is a console based E-commerce application

Install Protocol Buffer in your system

In terminal, run
    protoc --java_out="src/main/java" --proto_path="src/main/proto" src/main/proto/User.proto
    protoc --java_out="src/main/java" --proto_path="src/main/proto" src/main/proto/Product.proto
    protoc --java_out="src/main/java" --proto_path="src/main/proto" src/main/proto/Order.proto