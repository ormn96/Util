package GO

import "encoding/json"

//check if the first map (expectedMap) is completely exists inside the second map (mainMap).
//if false, return maps of the actual values, and the expected values
func MapContains(expectedMap, mainMap map[string]interface{}) (contains bool, actualDiff, expectedDiff map[string]interface{}) {
	contains = true
	actualDiff = make(map[string]interface{})
	expectedDiff = make(map[string]interface{})
	for eKey, eValue := range expectedMap { //go to every field in expectedMap
		aValue, aExists := mainMap[eKey]
		if !aExists { //if the key not exist in mainMap
			expectedDiff[eKey] = eValue
			actualDiff[eKey] = "#NOT_FOUND#"
			contains = false
			continue
		}
		//START deep contains
		mapEValue, okE := eValue.(map[string]interface{})
		mapAValue, okA := aValue.(map[string]interface{})
		if okA && okE { // if both values are of type map[string]interface{} , AKA other unknown struct
			if recContains, recActual, recExpected := MapContains(mapEValue, mapAValue); !recContains {
				expectedDiff[eKey] = recExpected
				actualDiff[eKey] = recActual
				contains = false
			}
			//END deep contains
		} else if eValue != aValue { // if not struct, check if equals
			expectedDiff[eKey] = eValue
			actualDiff[eKey] = aValue
			contains = false
		}
	}
	return
}

func DeepContains(expectedObj, mainObj interface{}) (contains bool, actualDiff, expectedDiff map[string]interface{}, err error) {
	strMap1, eMap1 := json.Marshal(expectedObj)
	if eMap1 != nil {
		return false, nil, nil, err
	}
	strMap2, eMap2 := json.Marshal(mainObj)
	if eMap2 != nil {
		return false, nil, nil, err
	}
	var map1, map2 interface{}

	jsonErr1 := json.Unmarshal(strMap1, &map1)
	if jsonErr1 != nil {
		return false, nil, nil, err
	}
	jsonErr2 := json.Unmarshal(strMap2, &map2)
	if jsonErr2 != nil {
		return false, nil, nil, err
	}
	retContains, retActual, retExpected := MapContains(map1.(map[string]interface{}), map2.(map[string]interface{}))
	return retContains, retActual, retExpected, nil
}
