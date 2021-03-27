NAME=embers

## Makefile so I don't have to remember those import command line commands.

.PHONY: help verify build docs

help: ## show targets plus comment
	@egrep -h '\s##\s' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-20s\033[0m %s\n", $$1, $$2}'

verify: ## run this before check-in
	./gradlew clean build

build: verify ## circle ci runs this
	./gradlew codeCoverageReport

docs: verify ## copy the yatspec output to the docs folder
	cp embers-acceptance-tests/build/yatspec/adf/embers/acceptance/** docs/
	cp embers-acceptance-tests/build/yatspec/adf/embers/e2e/*.html docs/
