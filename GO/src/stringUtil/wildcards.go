package stringUtil

import (
	"fmt"
	"math/rand"
	"regexp"
	"strconv"
	"strings"
	"time"
)

var ner = 1

const charSet = "abcdefghijklmnopqrstuvwxyz0123456789,.~ /\\[]()!@#$%^&*-=_+אבגדהוזחטיכלמנסעפצקרשתףםןץך"
const engCharSet = "abcdefghijklmnopqrstuvwxyz0123456789"
const hebCharSet = "אבגדהוזחטיכלמנסעפצקרשתףםןץך0123456789"

var r = rand.New(rand.NewSource(time.Now().UnixNano()))

func ApplyWildcard(input []byte) []byte {
	str := string(input)
	//#TIME_NOW#
	now, _ := time.Now().MarshalText()
	nowStr := string(now)
	str = strings.ReplaceAll(str, "#TIME_NOW#", nowStr)
	//#TIME_NOW#

	//#NER#
	if strings.Contains(str, "#NER#") {
		str = strings.ReplaceAll(str, "#NER#", fmt.Sprint(ner))
		ner = ner + 1
	}
	//#NER#

	//#RAND_NUM#
	str, _ = ReplaceForEachUniqueRegex(wildcardHelpers("#|size|RAND_NUM|ser|#"), str, func(s string) string {
		size, found := retrieveNumber(s)
		if found {
			return fmt.Sprint(r.Intn(size))
		}
		return fmt.Sprint(r.Uint32())
	})
	//#RAND_NUM#

	//#RAND_TEXT#
	str, _ = ReplaceForEachUniqueRegex(wildcardHelpers("#|size|RAND_TEXT|ser|#"), str, func(s string) string {
		ch := []rune(charSet)
		var newText []rune
		size, _ := retrieveNumber(s)
		for i := 0; i < size; i++ {
			v := ch[r.Intn(len(ch))]
			newText = append(newText, v)
		}
		return string(newText)
	})
	//#RAND_TEXT#

	//#RAND_TEXT_ENG#
	str, _ = ReplaceForEachUniqueRegex(wildcardHelpers("#|size|RAND_TEXT_ENG|ser|#"), str, func(s string) string {
		ch := []rune(engCharSet)
		var newText []rune
		size, _ := retrieveNumber(s)
		for i := 0; i < size; i++ {
			v := ch[r.Intn(len(ch))]
			newText = append(newText, v)
		}
		return string(newText)
	})
	//#RAND_TEXT_ENG#

	//#RAND_TEXT_HEB#
	str, _ = ReplaceForEachUniqueRegex(wildcardHelpers("#|size|RAND_TEXT_HEB|ser|#"), str, func(s string) string {
		ch := []rune(hebCharSet)
		var newText []rune
		size, _ := retrieveNumber(s)
		for i := 0; i < size; i++ {
			v := ch[r.Intn(len(ch))]
			newText = append(newText, v)
		}
		return string(newText)
	})
	//#RAND_TEXT_HEB#
	return []byte(str)
}

func wildcardHelpers(pattern string) string {
	pattern = strings.ReplaceAll(pattern, "|size|", "(\\([0-9]+\\))?")
	pattern = strings.ReplaceAll(pattern, "|ser|", "[0-9]*")
	return pattern
}

const defaultLenSize int = 30

func retrieveNumber(str string) (int, bool) {
	reg, e := regexp.Compile("(\\([1-9][0-9]*\\))")
	if e != nil {
		return defaultLenSize, false
	}
	num := reg.FindString(str)
	num = strings.TrimPrefix(num, "(")
	num = strings.TrimSuffix(num, ")")
	number, e := strconv.Atoi(num)
	if e != nil {
		return defaultLenSize, false
	}
	return number, true
}
