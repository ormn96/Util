package cli

import (
	"fmt"
	"regexp"
	"stringUtil"
	"strings"
)

var base = commandTreeNode{
	keyWord:   "",
	parameter: []string{},
	run: func(s map[string]string) error {
		return nil
	},
	children: make(map[string]*commandTreeNode),
}

type commandTreeNode struct {
	keyWord   string
	parameter []string
	run       func(map[string]string) error
	children  map[string]*commandTreeNode
}

func Run(cmd []string) error {
	return base.runCmdRec(cmd, make(map[string]string))
}

func (n commandTreeNode) runCmdRec(cmd []string, parametersList map[string]string) error {
	if len(cmd) == len(n.parameter) {
		for i, v := range n.parameter {
			parametersList[v] = cmd[i]
		}
		return n.run(parametersList)
	} else if len(cmd) < len(n.parameter) {
		return fmt.Errorf("not enough parameters\n")
	}

	if len(n.parameter) != 0 {
		for i, v := range n.parameter {
			parametersList[v] = cmd[i]
		}
		cmd = cmd[len(n.parameter):]
	}
	nextCmd, ex := n.children[cmd[0]]
	if !ex {
		return fmt.Errorf("command part \"%s\" not recognized\n", cmd[0])
	}

	return nextCmd.runCmdRec(cmd[1:], parametersList)
}

func MapNewCommand(cmd string, toRun func(map[string]string) error) {
	r, _ := regexp.Compile("#.*#")
	split, _ := splitToWords(cmd)
	cur := &base
	for i := 0; i < len(split); i++ {
		next, ex := cur.children[split[i]]
		if !ex {
			tmp := commandTreeNode{
				keyWord:   "",
				parameter: []string{},
				run:       nil,
				children:  make(map[string]*commandTreeNode),
			}
			cur.children[split[i]] = &tmp
			j := i + 1
			for ; j < len(split); j++ {
				if !r.MatchString(split[j]) {
					break
				}
			}
			tmp.parameter = stringUtil.RunOnArray(split[i+1:j], func(s string) string {
				return stringUtil.Unwrap(s, "#")
			})
			i = j - 1
			next = &tmp
		}
		cur = next
	}
	cur.run = toRun
}

func AddGlobalHelpMessage(msg string) {
	base.run = func(m map[string]string) error {
		println(msg)
		return nil
	}
}

func splitToWords(str string) ([]string, error) {
	split := strings.Split(str, " ")
	ret := make([]string, 0, len(split))
	for i := 0; i < len(split); i++ {
		if !strings.HasPrefix(split[i], "\"") {
			ret = append(ret, split[i])
		} else {
			j := i
			for ; j < len(split); j++ {
				if strings.HasSuffix(split[i], "\"") {
					long := strings.Join(split[i:j], " ")
					long = strings.TrimPrefix(long, "\"")
					long = strings.TrimSuffix(long, "\"")
					ret = append(ret, long)
					i = j
					break
				}
			}
			return nil, fmt.Errorf("Wrong syntax, found open '\"' but not closed\n")
		}
	}
	return ret, nil
}
