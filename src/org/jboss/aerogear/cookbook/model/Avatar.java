/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jboss.aerogear.cookbook.model;

/**
 *
 * @author summers
 */
public interface Avatar<T> {
    public Long getId();

    public String getName();

    public T getPhoto();
}
