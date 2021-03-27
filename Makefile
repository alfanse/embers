NAME=embers

.PHONY: help verify
help: ## Show this help
	@egrep -h '\s##\s' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-20s\033[0m %s\n", $$1, $$2}'

verify: ## run this before check-in
	./gradlew clean build

build: verify ## circle ci runs this
	./gradlew codeCoverageReport
