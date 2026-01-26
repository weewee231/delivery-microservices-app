plugins {
	java
	id("org.springframework.boot") version "3.5.7"
	id("io.spring.dependency-management") version "1.1.7"
}

version = "1.0.0"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

dependencies {
	implementation(project(":common-libs"))

	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	
	// HTTP клиенты к другим сервисам
	implementation("org.springframework.boot:spring-boot-starter-webflux")

	testImplementation("org.springframework.boot:spring-boot-starter-test")

	// lombok
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	testCompileOnly("org.projectlombok:lombok")
	testAnnotationProcessor("org.projectlombok:lombok")

	// swagger
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

