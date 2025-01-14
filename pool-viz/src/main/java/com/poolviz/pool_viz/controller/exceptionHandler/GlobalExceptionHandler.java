package com.poolviz.pool_viz.controller.exceptionHandler;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import wf.bitcoin.javabitcoindrpcclient.BitcoinRPCException;

import java.net.NoRouteToHostException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoRouteToHostException.class)
    public ModelAndView handleNoRouteToHostException(NoRouteToHostException exception) {

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("error", "No route to host");
        modelAndView.addObject("message", "Unable to connect to the Bitcoin node. Please check the server's network connectivity.");

        modelAndView.setViewName("errorPage");

        return modelAndView;
    }

    @ExceptionHandler(BitcoinRPCException.class)
    public ModelAndView handleBitcoinRPCException(BitcoinRPCException exception) {

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("error", "Bitcoin RPC error");
        modelAndView.addObject("message", "An error occurred while interacting with the Bitcoin RPC client: " + exception.getMessage());

        modelAndView.setViewName("errorPage");

        return modelAndView;
    }
}
