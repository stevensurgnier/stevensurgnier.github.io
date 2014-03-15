;; -*- coding: utf-8 -*-
;;
;; © 2014 Steven Surgnier
;; Author: Steven Surgnier <stevensurgnier@gmail.com>
;;

(ns ssblog.parser)

(defn parse-step
  "Lazily apply the parsers to the string returning the first parser
   that returns a non nil first element."
  [parsers string]
  (first (remove (comp nil? first) (map #(% string) parsers))))

(defn parser
  "Returns a function that accepts a string to parse.
   PARSERS must be a collection of functions where each function accepts a
   string and returns a collection of [output rest-string]. COMBINE must be a
   function of two arguments. At each step of parsing COMBINE is applied to
   the output of the current and previous step to produce the new output.
   When the INPUT string has been fully consumed the result of the last
   application of COMBINE is returned. VAL can optionally be provided as the
   initial first argument to COMBINE. The default values of COMBINE and VAL
   are conj and [], respectively."
  ([parsers] (parser parsers conj))
  ([parsers combine] (parser parsers combine []))
  ([parsers combine val]
     (fn [input]
       (loop [output val
              string input]
         (let [[step-output rest-string] (parse-step parsers string)
               new-output (combine output step-output)]
           (if (or (empty? rest-string)
                   (when (= string rest-string)
                     (println "Failed to consume the input string.")
                     true))
             new-output
             (recur new-output rest-string)))))))
