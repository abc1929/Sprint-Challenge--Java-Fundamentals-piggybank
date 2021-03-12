package com.lambdaschool.piggybank.controllers;

import com.lambdaschool.piggybank.models.Piggybank;
import com.lambdaschool.piggybank.repositories.PiggybankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class PiggybankController {

    public static String GetLine(Piggybank i) {
        if (i.getQuantity() > 1) {
            return i.getQuantity() + " " + i.getNameplural();
        }
        return i.getQuantity() + " " + i.getName();

    }

    @Autowired
    PiggybankRepository piggyrepo;


    @GetMapping(value = "/total", produces = {"application/json"})
    public ResponseEntity<?> GetTotal() {
        List<Piggybank> myList = new ArrayList<>();
        piggyrepo.findAll().iterator().forEachRemaining(myList::add);
        myList.forEach(i -> System.out.println(GetLine(i)));
        System.out.println("The piggy bank holds " + myList.stream().map(i -> i.getValue() * i.getQuantity()).reduce(Double::sum).get());

        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
    }

    @GetMapping(value = "/coins", produces = {"application/json"})
    public ResponseEntity<?> GetAll() {
        return new ResponseEntity<>(piggyrepo.findAll(), HttpStatus.OK);
    }


    @PutMapping(value = "/money/{amount}")
    public ResponseEntity<?> updateCoin(@PathVariable double amount) throws Exception {

        // 0.01 0.05 0.1 0.25 1
        int[] init = {0, 0, 0, 0, 0};


        piggyrepo.findAll().iterator().forEachRemaining(i -> {
            switch (i.getValue() + "") {
                case "0.01":
                    init[0] += i.getQuantity();
                    break;
                case "0.05":
                    init[1] += i.getQuantity();
                    break;
                case "0.1":
                    init[2] += i.getQuantity();
                    break;
                case "0.25":
                    init[3] += i.getQuantity();
                    break;
                case "1.0":
                    init[4] += i.getQuantity();
                    break;
                default:
                    break;
            }
        });

        System.out.println(init[0] + " " + init[1] + " " + init[2] + " " + init[3] + " " + init[4]);

        HashMap<Double, Integer[]> allcombo = new HashMap<>();
        // brute force for now
        for (int i0 = 0; i0 < init[0]; i0++) {
            for (int i1 = 0; i1 < init[1]; i1++) {
                for (int i2 = 0; i2 < init[2]; i2++) {
                    for (int i3 = 0; i3 < init[3]; i3++) {
                        for (int i4 = 0; i4 < init[4]; i4++) {
                            double key = i0 * 0.01 + i1 * 0.05 + i2 * 0.1 + i3 * 0.25 + i4;
                            if (allcombo.get(key) == null) {
                                // we just set one solution on the fly
                                allcombo.put(key, new Integer[]{i0, i1, i2, i3, i4});
                            }
                        }
                    }
                }
            }
        }

        List<Piggybank> myList = new ArrayList<>();
        piggyrepo.findAll().iterator().forEachRemaining(myList::add);
//           (1, 'Quarter', 'Quarters', 0.25, 1),
//            (2, 'Dime', 'Dimes', 0.10, 1),
//            (3, 'Dollar', 'Dollars', 1.00, 5),
//            (4, 'Nickel', 'Nickels', 0.05, 3),
//            (5, 'Dime', 'Dimes', 0.10, 7),
//            (6, 'Dollar', 'Dollars', 1.00, 1),
//            (7, 'Penny', 'Pennies', 0.01, 10);
        var a = allcombo.get(amount);


        if (allcombo.get(amount) != null) {

            for (int i = 0; i < a.length; i++) {
                if (a[i] != 0) {
                    switch (i) {
                        case 0: {
                            System.out.println("Removing " + a[i] + " Pennies");
                            piggyrepo.saveAll(RemoveCoins(myList, a, 0));
                        }
                        case 1: {
                            System.out.println("Removing " + a[i] + " Nickels");
                            piggyrepo.saveAll(RemoveCoins(myList, a, 1));
                            break;
                        }
                        case 2: {
                            System.out.println("Removing " + a[i] + " Dimes");
                            piggyrepo.saveAll(RemoveCoins(myList, a, 2));
                            break;
                        }
                        case 3: {
                            System.out.println("Removing " + a[i] + " Quarters");
                            piggyrepo.saveAll(RemoveCoins(myList, a, 3));
                            break;
                        }
                        case 4: {
                            System.out.println("Removing " + a[i] + " Dollars");
                            piggyrepo.saveAll(RemoveCoins(myList, a, 4));
                            break;
                        }
                        default:
                            break;
                    }
                }
            }


        } else {
            return ResponseEntity.ok("Money not available");
        }


        return ResponseEntity.ok(piggyrepo.findAll());


    }



    private List<Piggybank> RemoveCoins(List<Piggybank> myList, Integer[] a, int i) {
        String h = "";
        switch (i) {
            case 0:
                h = "Pennies";
                break;
            case 1:
                h = "Nickels";
                break;
            case 2:
                h = "Dimes";
                break;
            case 3:
                h = "Quarters";
                break;
            case 4:
                h = "Dollars";
                break;
            default:
                break;
        }

        String finalH = h;
        // hacking a bit here since we know we have up to duplicates of 2;

        var xx = myList.stream().filter(j -> j.getNameplural().equals(finalH)).collect(Collectors.toList()).get(0).getId();
        if (piggyrepo.findById(xx).isPresent()) {
            Piggybank x = piggyrepo.findById(xx).get();

            if (x.getQuantity() < a[0]) {
                x.setQuantity(0);
                piggyrepo.save(x);
                Piggybank y = piggyrepo.findById(myList.stream().filter(j -> j.getNameplural().equals(finalH)).collect(Collectors.toList()).get(1).getId()).get();
                y.setQuantity(y.getQuantity() - (a[0] - x.getQuantity()));
                piggyrepo.save(y);
            } else {
                x.setQuantity(x.getQuantity() - a[0]);
                piggyrepo.save(x);
            }
        }
        return myList;


    }


}