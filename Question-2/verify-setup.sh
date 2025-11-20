#!/bin/bash

echo "========================================="
echo "Product Service - Setup Verification"
echo "========================================="
echo ""

# Check Java
echo "Checking Java..."
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1)
    echo "✓ Java found: $JAVA_VERSION"
    
    # Check if Java 17 or higher
    JAVA_MAJOR=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$JAVA_MAJOR" -ge 17 ]; then
        echo "✓ Java version is 17 or higher"
    else
        echo "✗ Java version is less than 17. Please install Java 17 or higher."
        echo "  Current version: $JAVA_VERSION"
    fi
else
    echo "✗ Java not found. Please install Java 17 or higher."
fi
echo ""

# Check Maven
echo "Checking Maven..."
if command -v mvn &> /dev/null; then
    MVN_VERSION=$(mvn -version | head -n 1)
    echo "✓ Maven found: $MVN_VERSION"
else
    echo "✗ Maven not found. Please install Maven 3.6 or higher."
    echo "  Install with: brew install maven (macOS) or apt install maven (Linux)"
fi
echo ""

# Check Docker
echo "Checking Docker..."
if command -v docker &> /dev/null; then
    DOCKER_VERSION=$(docker --version)
    echo "✓ Docker found: $DOCKER_VERSION"
else
    echo "✗ Docker not found. Please install Docker Desktop."
fi
echo ""

# Check Docker Compose
echo "Checking Docker Compose..."
if command -v docker-compose &> /dev/null; then
    COMPOSE_VERSION=$(docker-compose --version)
    echo "✓ Docker Compose found: $COMPOSE_VERSION"
else
    echo "✗ Docker Compose not found. Please install Docker Desktop."
fi
echo ""

# Check project files
echo "Checking project files..."
FILES=(
    "pom.xml"
    "TestExampleFile.csv"
    "database/docker-compose.yml"
    "src/main/java/com/example/productservice/ProductServiceApplication.java"
    "src/main/resources/application.properties"
    "README.md"
)

for file in "${FILES[@]}"; do
    if [ -f "$file" ]; then
        echo "✓ $file"
    else
        echo "✗ $file (missing)"
    fi
done
echo ""

# Summary
echo "========================================="
echo "Verification Complete"
echo "========================================="
echo ""
echo "If all checks passed, you can proceed with:"
echo "  1. cd database && docker-compose up -d"
echo "  2. mvn clean install"
echo "  3. mvn spring-boot:run"
echo ""
echo "For detailed instructions, see SETUP.md"

