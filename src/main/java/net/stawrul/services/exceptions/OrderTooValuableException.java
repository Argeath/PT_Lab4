/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.stawrul.services.exceptions;

/**
 *
 * @author student
 */
public class OrderTooValuableException extends RuntimeException {

    public OrderTooValuableException() {
        super("Cost sum > 150 PLN");
    }
}
