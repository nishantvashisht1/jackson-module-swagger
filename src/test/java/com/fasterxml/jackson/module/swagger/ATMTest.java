package com.fasterxml.jackson.module.swagger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.swagger.model.Model;

public class ATMTest extends SwaggerTestBase
{
    static class ATM {
//        private List<Currency> supportedCurrencies;

        private Currency currency;

        public void setCurrency(Currency currency) {
          this.currency = currency;
        }

        public Currency getCurrency() {
          return currency;
        }
    }

    public enum Currency { USA, CANADA }

    public void testATMModel() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        Model model = new ModelResolver(mapper)
             .resolve(ATM.class);
        assertNotNull(model);

//        System.out.println("ATM Model: "+model);    
    }
}

