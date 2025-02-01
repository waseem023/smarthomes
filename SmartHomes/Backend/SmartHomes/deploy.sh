#!/bin/bash

echo "Building and packaging the project..."
mvn clean package

echo "Deploying to Tomcat..."
mvn tomcat7:deploy

echo "Deployment complete!"
