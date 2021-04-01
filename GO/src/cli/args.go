package cli

import (
	"fmt"
	"os"
	"strings"
)

var initialized = false
var command []string
var arguments map[string]string
var flags map[string]struct{}
var defaults map[string]string = nil
var substitutes = make(map[string]string)
var helpMsg = `Message To Show if Typed  "--help" or "-?" , can be changed by SetGlobalHelpMsg(msg string)`
var helpers = make(map[string]string)

/*
scans the arguments(os.Args) and divide them to Command part and to arguments, if "--help" or "-?" will print help message based on the command or the global help message if not set
*/
func InitializeArguments() {

	arguments, flags, command = argumentParser()

	initialized = true

	if IsFlagged("--help") || IsFlagged("-?") {
		printHelp(strings.Join(command, " "))
		os.Exit(0)
	}
	if defaults != nil {
		for k, v := range defaults {
			if _, exists := arguments[k]; !exists {
				arguments[k] = v
			}
		}
	}
}

//helper function that parse the arguments
func argumentParser() (map[string]string, map[string]struct{}, []string) {
	ret := make(map[string]string)
	flags := make(map[string]struct{})
	arg := os.Args
	key := ""

	cmdCnt := 1
	//go past through the command part
	for ; cmdCnt < len(arg) && !strings.HasPrefix(arg[cmdCnt], "-"); cmdCnt++ {

	}
	cmdRet := arg[1:cmdCnt]

	for i := cmdCnt; i < len(arg); i++ {
		curArg := arg[i]
		//try to substitute the flag
		if subArg, ex := substitutes[curArg]; ex {
			curArg = subArg
		}
		if key == "" {
			if !(strings.HasPrefix(curArg, "-") || strings.HasPrefix(curArg, "-")) {
				return nil, nil, []string{}
			} else {
				key = curArg
			}
		} else {
			if !(strings.HasPrefix(curArg, "-") || strings.HasPrefix(curArg, "-")) {
				ret[key] = curArg
				key = ""
			} else {
				flags[key] = struct{}{}
				key = curArg
			}
		}

	}
	if key != "" {
		flags[key] = struct{}{}
	}
	return ret, flags, cmdRet
}

func SetArgumentsDefault(argsDefaults map[string]string) {
	if !initialized {
		InitializeArguments()
	}
	defaults = argsDefaults
}

func SetArgumentsSubstitute(argsSubstitutes map[string]string) {
	if !initialized {
		InitializeArguments()
	}
	substitutes = argsSubstitutes
}

func SetGlobalHelpMsg(msg string) {
	helpMsg = msg
}

func AddCommandHelpMsg(cmd, msg string) {

	helpers[cmd] = msg
}
func SetCommandsHelpMessages(help map[string]string) {
	helpers = help
}

//return if specific flag received
func IsFlagged(flag string) bool {
	if !initialized {
		InitializeArguments()
	}
	_, exist := flags[flag]
	return exist
}

//return the value of an argument and if the argument exists
func GetArg(argName string) (value string, exists bool) {
	if !initialized {
		InitializeArguments()
	}
	val, ex := arguments[argName]
	return val, ex
}

//return the command part of the arguments
func GetCommand() (cmd []string) {
	if !initialized {
		InitializeArguments()
	}
	return command
}

//return map of all the arguments
func GetAllArgs() (argumentList map[string]string) {
	if !initialized {
		InitializeArguments()
	}
	return arguments
}

//return array of all the flags
func GetAllFlags() []string {
	if !initialized {
		InitializeArguments()
	}
	keys := make([]string, 0, len(flags))
	for k, _ := range flags {
		keys = append(keys, k)
	}
	return keys
}

func printHelp(cmd string) {
	if msg, ex := helpers[cmd]; ex {
		fmt.Println(msg)
	} else {
		fmt.Println(helpMsg)
	}

}
