package contains

import (
	"encoding/json"
	"fmt"
	"time"
)

var TimeDiff = time.Second

//check if the first map (sub) is completely exists inside the second (main).
//if false, returns map with expected and actual
func MapContains(expectedMap, main map[string]interface{}) (contains bool, actualDiff map[string]interface{}, expectedDiff map[string]interface{}) {
	contains = true
	actualDiff = make(map[string]interface{})
	expectedDiff = make(map[string]interface{})
	for eKey, eValue := range expectedMap {
		aValue, aExist := main[eKey]
		if !aExist {
			expectedDiff[eKey] = eValue
			actualDiff[eKey] = "#NOT_FOUND#"
			contains = false
			continue
		}
		if eValue == "#ANY_VALUE#" {
			continue
		}
		//deep contains
		switch eValue.(type) {

		case map[string]interface{}:
			mapAValue, okA := aValue.(map[string]interface{})
			if okA { //object Deep contains
				if recContains, recExpected, recActual := MapContains(eValue.(map[string]interface{}), mapAValue); !recContains {
					expectedDiff[eKey] = recExpected
					actualDiff[eKey] = recActual
					contains = false
				}
				continue
			}

		case []interface{}:
			aValueArr, okAArr := aValue.([]interface{})
			arrResponse := make([]interface{}, 0)
			if okAArr { //array Deep contains
				for _, eArrValue := range eValue.([]interface{}) {
					found := false
					for _, aArrValue := range aValueArr {

						if recContains, _, _ := MapContains(
							map[string]interface{}{"": eArrValue},
							map[string]interface{}{"": aArrValue}); recContains {
							found = true
						}
					}
					if !found {
						arrResponse = append(arrResponse, eArrValue)
					}
				}
				if len(arrResponse) != 0 {
					expectedDiff[eKey] = arrResponse
					actualDiff[eKey] = aValue
					contains = false
				}
				continue
			}

		case string:
			var eValTime, aValTime time.Time
			aValueStr, okAStr := aValue.(string)
			if okAStr {
				e1 := eValTime.UnmarshalText([]byte(eValue.(string)))
				e2 := aValTime.UnmarshalText([]byte(aValueStr))
				if e1 == nil && e2 == nil {
					if eValTime.Round(TimeDiff) != aValTime.Round(TimeDiff) {
						expectedDiff[eKey] = eValue
						actualDiff[eKey] = aValue
						contains = false
					}
					continue
				}
			}

		}

		//END deep contains
		if eValue != aValue {
			expectedDiff[eKey] = eValue
			actualDiff[eKey] = aValue
			contains = false
		}
	}
	return
}

//check if the first object (sub) is completely exists inside the second (main), AKA every attribute in sub equals to the same attribute in main(if non primitive type, check recursively).
//if false, returns map with expected and actual.
//error returned if there is a problem while marshaling(JSON)
func DeepContains(sub, main interface{}) (contains bool, actualDiff map[string]interface{}, expectedDiff map[string]interface{}, err error) {
	subStr, eSub := json.Marshal(sub)
	if eSub != nil {
		return false, nil, nil, ContainsError{"Failed to marshal sub Object", eSub}
	}
	mainStr, eMain := json.Marshal(main)
	if eMain != nil {
		return false, nil, nil, ContainsError{"Failed to marshal main Object", eMain}
	}

	return DeepContainsJson(subStr, mainStr)
}

//check if the first object (sub) is completely exists inside the second (main), AKA every attribute in sub equals to the same attribute in main(if non primitive type, check recursively).
//if false, returns map with expected and actual.
//error returned if there is a problem while marshaling(JSON)
func DeepContainsJson(subJson, mainJson []byte) (contains bool, actualDiff map[string]interface{}, expectedDiff map[string]interface{}, err error) {
	var subInt, mainInt interface{}
	if e := json.Unmarshal(subJson, &subInt); e != nil {
		return false, nil, nil, ContainsError{"Failed to unmarshal sub Object", e}
	}

	if e := json.Unmarshal(mainJson, &mainInt); e != nil {
		return false, nil, nil, ContainsError{"Failed to unmarshal main Object", e}
	}
	contains, actualDiff, expectedDiff = MapContains(subInt.(map[string]interface{}), mainInt.(map[string]interface{}))
	err = nil
	return
}

type ContainsError struct {
	message string
	cause   error
}

func (e ContainsError) Error() string {
	return fmt.Sprintf("%v, cause: %v", e.message, e.cause)
}

func EncapsulateReportFail(receivedMsg, expectedMsg []byte, actual, expected interface{}) ContainsReport {
	var em, rm interface{}

	if json.Unmarshal(receivedMsg, &rm) != nil || json.Unmarshal(expectedMsg, &em) != nil {
		return ContainsReport{
			Status:      "failed",
			ReceivedMsg: string(receivedMsg),
			ExpectedMsg: string(expectedMsg),
			Difference: ContainsReportDiff{
				Actual:   actual,
				Expected: expected,
			},
		}
	} else {
		return ContainsReport{
			Status:      "failed",
			ReceivedMsg: rm,
			ExpectedMsg: em,
			Difference: ContainsReportDiff{
				Actual:   actual,
				Expected: expected,
			},
		}
	}

}

type ContainsReport struct {
	Status      string
	ReceivedMsg interface{}
	ExpectedMsg interface{}
	Difference  ContainsReportDiff
}

type ContainsReportDiff struct {
	Actual   interface{}
	Expected interface{}
}

func (r ContainsReport) ToJson() string {
	j, e := json.Marshal(r)
	if e != nil {
		return "error while marshaling report - " + e.Error()
	}
	return string(j)
}
