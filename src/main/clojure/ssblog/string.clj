;; -*- coding: utf-8 -*-
;;
;; © 2014 Steven Surgnier
;; Author: Steven Surgnier <stevensurgnier@gmail.com>
;;

(ns ssblog.string)

(defn if-empty-then-nil
  "If S is empty then return nil else return S."
  [s]
  (if (empty? s) nil s))

(defn get-leading-char-count
  "Return the count of repeated leading characters in a given string."
  ([string] (get-leading-char-count (first string) string))
  ([a string]
     (let [p #(= a (if (char? a) % (str %)))]
       (count (take-while p string)))))

(defn get-trailing-char-count
  "Return the count of repeated trailing characters in a given string."
  ([string] (get-leading-char-count (reverse string)))
  ([a string]
     (get-leading-char-count a (reverse string))))

(defn rep
  "Return a string of S repeated N times."
  [n s]
  (apply str (repeat n s)))

(defn break
  "Break a string into a vector of two elements. Default behavior is to
   return the matched string at the tail of the left element. If :r is set
   to true then the matched string will be returned at the head of the
   right element."
  [a string & {:keys [r] :or {r false}}]
  (cond
   (nil? string) [nil string]

   (instance? java.util.regex.Pattern a)
   (let [m (re-matcher a string)]
     (if-not (.find m)
       [string]
       (let [index (if r (.start m) (.end m))
             fst (subs string 0 index)
             sec (subs string index)]
         [(if-empty-then-nil fst)
          (if-empty-then-nil sec)])))

   (string? a)
   (let [start (.indexOf string a)]
     (if (>= start 0)
       (let [index (if r start (+ start (count a)))]
         [(subs string 0 index) (subs string index)])
       [nil string]))

   (integer? a)
   (let [index (if r (dec a) a)]
     [(subs string 0 index) (subs string index)])

   :else [nil string]))
