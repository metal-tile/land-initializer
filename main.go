package main

import (
	"encoding/json"
	"flag"
	"log"
	"os"
)

// MapChip is 1つのMapChipが持つ情報
type MapChip struct {
	Row      int     `json:"row"`
	Col      int     `json:"col"`
	Chip     int     `json:"chip"`
	HitPoint float64 `json:"hitPoint"`
}

func main() {
	o := flag.String("o", "/tmp/hoge.json", "output file path")
	rowSize := flag.Int("row", 50, "output row size")
	colSize := flag.Int("col", 40, "output col size")
	flag.Parse()

	file, err := os.OpenFile(*o, os.O_APPEND|os.O_WRONLY, 0600)
	if err != nil {
		log.Fatalf("fatal open file. %s", err.Error())
	}
	defer file.Close()

	for row := 0; row < *rowSize; row++ {
		for col := 0; col < *colSize; col++ {
			v := MapChip{
				Row:      row,
				Col:      col,
				Chip:     0,
				HitPoint: 1000.0,
			}
			b, err := json.Marshal(v)
			if err != nil {
				log.Fatalf("fatal json marshal. %v, %s", v, err.Error())
			}
			file.Write(b)
			file.WriteString("\n")
		}
	}
}
