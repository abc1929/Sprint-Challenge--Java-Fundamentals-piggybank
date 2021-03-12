package com.lambdaschool.piggybank.controllers;

import com.lambdaschool.piggybank.models.Piggybank;
import com.lambdaschool.piggybank.repositories.PiggybankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class PiggybankController {

    public static String GetLine(Piggybank i){
        if(i.getQuantity()>1){
            return i.getQuantity()+" "+i.getNameplural();
        }
        return i.getQuantity()+" "+i.getName();

    }

    @Autowired
    PiggybankRepository piggyrepo;

    @GetMapping(value = "/total", produces = {"application/json"})
    public ResponseEntity<?> GetTotal() {
        List<Piggybank> myList = new ArrayList<Piggybank>();
        piggyrepo.findAll().iterator().forEachRemaining(myList::add);
        myList.forEach(i -> System.out.println(GetLine(i)));
        System.out.println("The piggy bank holds "+ myList.stream().map(i -> i.getValue()*i.getQuantity()).reduce((a,b) -> a+b).get());

        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
    }


}
