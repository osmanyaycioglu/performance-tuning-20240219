package com.adenon.api.smpp.sdk;

public class AddressDescriptor {

    private String               number;
    private ETypeOfNumber        ton = ETypeOfNumber.INTERNATIONAL;
    private ENumberPlanIndicator npi = ENumberPlanIndicator.ISDN;

    public String getNumber() {
        return this.number;
    }

    public AddressDescriptor setNumber(final String number) {
        this.number = number;
        return this;
    }

    public ETypeOfNumber getTon() {
        return this.ton;
    }

    public AddressDescriptor setTon(final ETypeOfNumber ton) {
        this.ton = ton;
        return this;
    }

    public ENumberPlanIndicator getNpi() {
        return this.npi;
    }

    public AddressDescriptor setNpi(final ENumberPlanIndicator npi) {
        this.npi = npi;
        return this;
    }

    @Override
    public String toString() {
        return "AddressDescriptor [number=" + this.number + ", ton=" + this.ton + ", npi=" + this.npi + "]";
    }

    public static AddressDescriptor getDefaultInternationalAdressDescriptor(final String number) {
        final AddressDescriptor addressDescriptor = new AddressDescriptor();
        addressDescriptor.setNumber(number);
        addressDescriptor.setNpi(ENumberPlanIndicator.ISDN);
        addressDescriptor.setTon(ETypeOfNumber.INTERNATIONAL);
        return addressDescriptor;
    }

    public static AddressDescriptor getDefaultNationalAdressDescriptor(final String number) {
        final AddressDescriptor addressDescriptor = new AddressDescriptor();
        addressDescriptor.setNumber(number);
        addressDescriptor.setNpi(ENumberPlanIndicator.ISDN);
        addressDescriptor.setTon(ETypeOfNumber.NATIONAL);
        return addressDescriptor;
    }

    public static AddressDescriptor getDefaultAlphanumericAdressDescriptor(final String number) {
        final AddressDescriptor addressDescriptor = new AddressDescriptor();
        addressDescriptor.setNumber(number);
        addressDescriptor.setNpi(ENumberPlanIndicator.ISDN);
        addressDescriptor.setTon(ETypeOfNumber.ALPHANUMERIC);
        return addressDescriptor;
    }
}
