package com.intrbiz.balsa.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.intrbiz.balsa.parameter.Parameter;

/**
 * A set of parameters, of a request, etc
 */
public interface ParameterSet
{
    /**
     * Get a map containing all parameters of this set indexed by parameter name
     * @return a Map of name to parameter mappings
     */
    Map<String, Parameter> getParameters();

    /**
     * Get a specific parameter
     * @param name the parameter name
     * @return the parameter or null if it does not exist
     */
    Parameter getParameter(String name);

    /**
     * Add the given parameter to this set, 
     * should the given parameter already exist it is replaced.
     * @param parameter the parameter to add
     */
    void addParameter(Parameter parameter);
    
    /**
     * Remove a parameter
     * @param parameter the parameter name to remove 
     */
    void removeParameter(String name);

    /**
     * Get all the names of all parameters contained in this set
     * @return the parameter names
     */
    Set<String> getParameterNames();

    /**
     * Get all the parameters of this set
     * @return the parameters
     */
    Collection<Parameter> getParameterValues();

    /**
     * Does this set contain the given parameter name
     * @param name the parameter name
     * @return true if a parameter of the given name exists within this set
     */
    boolean containsParameter(String name);
}