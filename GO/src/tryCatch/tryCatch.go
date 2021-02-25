package tryCatch

type Catch struct {
	matcher Matcher
	run     func()
}

type Matcher interface {
	Check(r interface{}) bool
}

/*
try catch as in Java for example,
try to match any of the catches , the first that match will execute his run function.
if no catches entered, just returns(act as catch all without run function)
*/
func TryCatch(try func(), catches ...Catch) {
	defer func() {
		r := recover() //try to catch
		if r == nil {  //if not panicked
			return
		}
		if len(catches) == 0 { //if no catches exists
			return
		}
		for _, ca := range catches {
			if ca.matcher.Check(r) {
				ca.run()
				return
			}
		}
		panic(r) //if no catch matched
	}()
	try()
}

/*
try catch as in Java for example but with finally func,
try to match any of the catches , the first that match will execute his run function.
if no catches entered, just returns(act as catch all without run function)
*/
func TryCatchFinally(try func(), finally func(), catches ...Catch) {
	defer finally()
	TryCatch(try, catches...)
}
