package com.example.swishbddriver.Model;

public class DriverInfo {
    private int id;
    private String driver_id;
    private String date_of_birth;
    private String partner_name;
    private String dl_name;
    private String dl_expire_date;
    private String tt_number;
    private String tt_expire_date;
    private String car_name;
    private String car_model;
    private String production_year;
    private String registration_year;
    private String plate_number;
    private String ins_expire_date;
    private String rp_expire_date;
    private String car_owner;
    private String nid_front;
    private String nid_back;
    private String driving_licence_photo;
    private String car_inside_photos;
    private String car_outside_photos;
    private String insurance_photos;
    private String tax_token_photos;
    private String car_registration_photos;
    private String road_permit_photos;

    public DriverInfo(int id, String driver_id, String date_of_birth, String partner_name, String dl_name, String dl_expire_date
            , String tt_number, String tt_expire_date, String car_name, String car_model, String production_year,
                      String registration_year, String plate_number, String ins_expire_date, String rp_expire_date,
                      String car_owner, String nid_front, String nid_back, String driving_licence_photo, String car_inside_photos,
                      String car_outside_photos, String insurance_photos, String tax_token_photos, String car_registration_photos,
                      String road_permit_photos) {
        this.id = id;
        this.driver_id = driver_id;
        this.date_of_birth = date_of_birth;
        this.partner_name = partner_name;
        this.dl_name = dl_name;
        this.dl_expire_date = dl_expire_date;
        this.tt_number = tt_number;
        this.tt_expire_date = tt_expire_date;
        this.car_name = car_name;
        this.car_model = car_model;
        this.production_year = production_year;
        this.registration_year = registration_year;
        this.plate_number = plate_number;
        this.ins_expire_date = ins_expire_date;
        this.rp_expire_date = rp_expire_date;
        this.car_owner = car_owner;
        this.nid_front = nid_front;
        this.nid_back = nid_back;
        this.driving_licence_photo = driving_licence_photo;
        this.car_inside_photos = car_inside_photos;
        this.car_outside_photos = car_outside_photos;
        this.insurance_photos = insurance_photos;
        this.tax_token_photos = tax_token_photos;
        this.car_registration_photos = car_registration_photos;
        this.road_permit_photos = road_permit_photos;
    }

    public DriverInfo() {
    }

    public int getId() {
        return id;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public String getDate_of_birth() {
        return date_of_birth;
    }

    public String getPartner_name() {
        return partner_name;
    }

    public String getDl_name() {
        return dl_name;
    }

    public String getDl_expire_date() {
        return dl_expire_date;
    }

    public String getTt_number() {
        return tt_number;
    }

    public String getTt_expire_date() {
        return tt_expire_date;
    }

    public String getCar_name() {
        return car_name;
    }

    public String getCar_model() {
        return car_model;
    }

    public String getProduction_year() {
        return production_year;
    }

    public String getRegistration_year() {
        return registration_year;
    }

    public String getPlate_number() {
        return plate_number;
    }

    public String getIns_expire_date() {
        return ins_expire_date;
    }

    public String getRp_expire_date() {
        return rp_expire_date;
    }

    public String getCar_owner() {
        return car_owner;
    }

    public String getNid_front() {
        return nid_front;
    }

    public String getNid_back() {
        return nid_back;
    }

    public String getDriving_licence_photo() {
        return driving_licence_photo;
    }

    public String getCar_inside_photos() {
        return car_inside_photos;
    }

    public String getCar_outside_photos() {
        return car_outside_photos;
    }

    public String getInsurance_photos() {
        return insurance_photos;
    }

    public String getTax_token_photos() {
        return tax_token_photos;
    }

    public String getCar_registration_photos() {
        return car_registration_photos;
    }

    public String getRoad_permit_photos() {
        return road_permit_photos;
    }
}
