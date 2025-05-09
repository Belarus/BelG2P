package org.alex73.fanetyka.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Very simple verbalizer - it just converts digits and latin chars into words.
 * Russian 'и' and 'щ' are also converted.
 */
public class SimpleVerbalizer {
    static final Map<Character, String> c2name = new HashMap<>();
    static {
        c2name.put('0', " нуль ");
        c2name.put('1', " адзін ");
        c2name.put('2', " два ");
        c2name.put('3', " тры ");
        c2name.put('4', " чытыры ");
        c2name.put('5', " пяць ");
        c2name.put('6', " шэсць ");
        c2name.put('7', " сем ");
        c2name.put('8', " восем ");
        c2name.put('9', " дзевяць ");
        c2name.put('A', " эй ");
        c2name.put('B', " бі ");
        c2name.put('C', " сі ");
        c2name.put('D', " дзі ");
        c2name.put('E', " і ");
        c2name.put('F', " эф ");
        c2name.put('G', " джы ");
        c2name.put('H', " эйч ");
        c2name.put('I', " ай ");
        c2name.put('J', " джэй ");
        c2name.put('K', " кей ");
        c2name.put('L', " эл ");
        c2name.put('M', " эм ");
        c2name.put('N', " эн ");
        c2name.put('O', " оў ");
        c2name.put('P', " пі ");
        c2name.put('Q', " кʼю ");
        c2name.put('R', " ар ");
        c2name.put('S', " эс ");
        c2name.put('T', " ці ");
        c2name.put('U', " ю ");
        c2name.put('V', " ві ");
        c2name.put('W', " даблʼю ");
        c2name.put('X', " экс ");
        c2name.put('Y', " уай ");
        c2name.put('Z', " зэд ");
        c2name.put('a', " эй ");
        c2name.put('b', " бі ");
        c2name.put('c', " сі ");
        c2name.put('d', " дзі ");
        c2name.put('e', " і ");
        c2name.put('f', " эф ");
        c2name.put('g', " джы ");
        c2name.put('h', " эйч ");
        c2name.put('i', " ай ");
        c2name.put('j', " джэй ");
        c2name.put('k', " кей ");
        c2name.put('l', " эл ");
        c2name.put('m', " эм ");
        c2name.put('n', " эн ");
        c2name.put('o', " оў ");
        c2name.put('p', " пі ");
        c2name.put('q', " кʼю ");
        c2name.put('r', " ар ");
        c2name.put('s', " эс ");
        c2name.put('t', " ці ");
        c2name.put('u', " ю ");
        c2name.put('v', " ві ");
        c2name.put('w', " даблʼю ");
        c2name.put('x', " экс ");
        c2name.put('y', " уай ");
        c2name.put('z', " зэд ");
        c2name.put('И', "і");
        c2name.put('и', "і");
        c2name.put('Щ', "шч");
        c2name.put('щ', "шч");
    }

    public String process(String input) {
        StringBuilder str = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (c2name.containsKey(c)) {
                str.append(c2name.get(c));
            } else {
                str.append(c);
            }
        }

        return str.toString().replaceAll("\\s+", " ");
    }
}
