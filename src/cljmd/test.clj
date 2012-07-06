(ns cljmd.test
  (:require [cljmd.core :as core]))

(def test-text
"> > foo

> bar
> baz

1. foo
2. bar
3. baz
4. 5

#bar
###bazw#ibble\r\n# wobble

#######flub

[msn]:    http://search.msn.com/
[google]: http://google.com/ \"The Google\"
[yahoo]:  http://yahoo.com")

(def test-section "> foo\r\n\r\n> bar\r\n> baz\r\ntest2\r\n 1. #lol!\r\n2. k")
(def test-section-1 "> foo\r\n> bar\r\n> baz\r\nwibble\r\n1. hola!\r\n 2.Hello!\r\n 3. rgr")

(println (core/build test-text))
