package com.zenika.zencontact.resource;

import com.googlecode.objectify.ObjectifyFilter;

import javax.servlet.annotation.WebFilter;

/**
 * Created by ssinigag on 20/11/17.
 */

@WebFilter(urlPatterns = {"/*"})
public class ObjectifyWebFilter extends ObjectifyFilter {
}
