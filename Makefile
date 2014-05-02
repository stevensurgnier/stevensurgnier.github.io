LEIN ?= lein

default: build

cljsbuild:
	$(LEIN) cljsbuild once

build:
	$(LEIN) do run, cljsbuild once
