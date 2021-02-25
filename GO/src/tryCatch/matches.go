package tryCatch

import "reflect"

/*
match the exception that thrown with the value received
*/
type ValueMatcher struct {
	Value interface{}
}

func (m ValueMatcher) Check(r interface{}) bool {
	return r == m.Value
}

//=============================================================

/*
match the exception that thrown with the type of the representative received
*/
type TypeMatcher struct {
	TypeRep interface{}
}

func (m TypeMatcher) Check(r interface{}) bool {
	return reflect.TypeOf(r).AssignableTo(reflect.TypeOf(m.TypeRep))
}

//=============================================================

/*
match the exception that thrown with the name of the type received
*/
type TypeNameMatcher struct {
	TypeName string
}

func (m TypeNameMatcher) Check(r interface{}) bool {
	return reflect.TypeOf(r).Name() == m.TypeName
}

//=============================================================

/*
match any thing that thrown
*/
type AnyMatcher struct {
}

func (m AnyMatcher) Check(r interface{}) bool {
	return true
}

//=============================================================

/*
match the exception that thrown with the function received
*/
type CustomMatcher struct {
	Checker func(interface{}) bool
}

func (m CustomMatcher) Check(r interface{}) bool {
	return m.Checker(r)
}
