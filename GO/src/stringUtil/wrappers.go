package stringUtil

import "strings"

//remove the given wrapper from the string
func Unwrap(str string, wrapper string) string {
	if !strings.HasPrefix(str, wrapper) || !strings.HasSuffix(str, wrapper) {
		return str
	}
	str = strings.TrimPrefix(str, wrapper)
	str = strings.TrimSuffix(str, wrapper)
	return str

}
