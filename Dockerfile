FROM openjdk:17
ADD target/bootstrapping-node.jar bootstrapping-node.jar
CMD ["java","-jar","bootstrapping-node.jar"]