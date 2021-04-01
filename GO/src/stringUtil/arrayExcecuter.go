package stringUtil

func RunOnArray(arr []string, f func(string) string) []string {
	ret := make([]string, len(arr))
	for i, str := range arr {
		ret[i] = f(str)
	}
	return ret
}
