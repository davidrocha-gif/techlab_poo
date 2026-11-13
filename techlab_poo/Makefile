# ==============================================================================
# MAKEFILE - TECHLAB INVENTORY & SERVICE DESK
# ==============================================================================

# Compilador e executável Java
JAVAC = javac
JAVA = java

# Pastas e arquivos
SRC_DIR = src
OUT_DIR = bin
LIB = lib/sqlite-jdbc-3_53_4_0-without-natives.jar:lib/sqlite-jdbc-3_53_4_0-natives-linux.jar
MAIN = Techlab.Main
FONTES = $(shell find $(SRC_DIR) -name "*.java")
FLAGS_JVM = -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8

.PHONY: all compila roda limpa

all: compila

compila:
	mkdir -p $(OUT_DIR)
	$(JAVAC) -encoding UTF-8 -d $(OUT_DIR) -cp $(LIB) $(FONTES)

roda: compila
	$(JAVA) $(FLAGS_JVM) -cp $(OUT_DIR):$(LIB) $(MAIN)

limpa:
	rm -rf $(OUT_DIR) techlab.db
