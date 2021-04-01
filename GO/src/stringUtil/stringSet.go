package stringUtil

import (
	"regexp"
	"strings"
)

type StringSet []string

func (a *StringSet) RemoveDuplicates() {
	val := *a
	helper := make(map[string]int)
	for _, str := range []string(val) {
		helper[str] = 1
	}

	res := make([]string, 0, len(helper))
	for setValues, _ := range helper {
		res = append(res, setValues)
	}
	*a = res
}

func ReplaceForEachUnique(nonUniqueValues []string, source string, replace func(string) string) string {
	uniqueValues := StringSet(nonUniqueValues)
	uniqueValues.RemoveDuplicates()
	str := source
	for _, value := range uniqueValues {
		str = strings.ReplaceAll(str, value, replace(value))
	}
	return str
}

func ReplaceForEachUniqueRegex(pattern string, source string, replace func(string) string) (string, error) {
	reg, e := regexp.Compile(pattern)
	if e != nil {
		return source, e
	}
	nonUniqueValues := reg.FindAllString(source, -1)
	return ReplaceForEachUnique(nonUniqueValues, source, replace), nil
}
