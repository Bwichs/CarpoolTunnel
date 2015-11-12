//
//  MapViewController.swift
//  
//
//  Created by Brian Wichers on 10/13/15.
//
//

import UIKit
import MapKit
import Parse
import CoreLocation

//custom button on each pin, this keeps track of the route's object id for use in RouteInfoView
class CustomPinButton: UIButton {
    var routeObjectID: String?
}

@available(iOS 8.0, *)
class PassengerViewController: UIViewController, MKMapViewDelegate, CLLocationManagerDelegate {
    var routeObjectID: String?
    
    @IBOutlet weak var menuButton: UIBarButtonItem!
    
    @IBOutlet weak var mapView: MKMapView!
    
    var locationManager = CLLocationManager()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.mapView.delegate = self
        
        self.locationManager.requestAlwaysAuthorization()
        self.locationManager.requestWhenInUseAuthorization()
        if CLLocationManager.locationServicesEnabled() {
            locationManager.delegate = self
            locationManager.desiredAccuracy = kCLLocationAccuracyBest
            locationManager.requestAlwaysAuthorization()
            locationManager.startUpdatingLocation()
        }
        
        
        /*executeWithAddressString("112 Peach Terrace, Santa Cruz, CA, 95060")
        { convertedLocation, error in
        self.convertCoordsToAddress(convertedLocation!)
        print (convertedLocation!.coordinate.longitude)
        print (convertedLocation!.coordinate.latitude)
        
        //OR handle the error appropriately
        }*/
        
        
        //Loop through querying Parse routes to drop pins
        let query = PFQuery(className:"ParseRoute")
        query.findObjectsInBackgroundWithBlock {
            (objects: [PFObject]?, error: NSError?) -> Void in
            
            if error == nil {
                // Do something with the found objects
                if let objects = objects as [PFObject]! {
                    for object in objects {
                        let fromAddress = object["from"] as! String
                        let toAddress = object["to"] as! String
                        let routeObjectID = object.objectId! as String
                        self.executeWithAddressString(fromAddress)
                            { convertedLocation, error in
                                self.addPin(convertedLocation!, pinLabel: toAddress, routeObjectID: routeObjectID)
                                //OR handle the error appropriately
                        }
                    }
                }
            } else {
                // Log details of the failure
                print("Error: \(error!) \(error!.userInfo)")
            }
        }
        
        
        //done with setting route on map*/
        
        
        
        // Do any additional setup after loading the view.
        if self.revealViewController() != nil {
            menuButton.target = self.revealViewController()
            menuButton.action = "revealToggle:"
            self.view.addGestureRecognizer(self.revealViewController().panGestureRecognizer())
        }
        self.revealViewController().rearViewRevealWidth = 160
    }
    
    //Updates user location to center initial mapview on self
    func locationManager(manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        let userLocation:CLLocation = locations[0]
        centerMapOnLocation(userLocation)
        locationManager.stopUpdatingLocation()
    }
    
    
    func locationManager(manager: CLLocationManager, didFailWithError error: NSError) {
        print("Error while updating location " + error.localizedDescription)
    }
    
    
    //Centers mapview around a provided location
    func centerMapOnLocation(location: CLLocation) {
        let regionRadius: CLLocationDistance = 1000
        let coordinateRegion = MKCoordinateRegionMakeWithDistance(location.coordinate,
            regionRadius * 2.0, regionRadius * 2.0)
        mapView.setRegion(coordinateRegion, animated: true)
    }
    
    
    //Adds a pin at a given location with a given label string and the objectID of the driver who created the route
    func addPin(location: CLLocation, pinLabel: String, routeObjectID: String){
        let dropPin = PinAnnotation(pinCoord: location.coordinate, routeObjectID: routeObjectID)
        dropPin.title = pinLabel
        mapView.addAnnotation(dropPin)
    }
   
    
    //Overrides pins on mapview to have custom looks and functionality
    func mapView(mapView: MKMapView, viewForAnnotation annotation: MKAnnotation) -> MKAnnotationView? {
        if annotation is PinAnnotation {
            let pinAnnotation = annotation as! PinAnnotation
            let pinAnnotationView = MKPinAnnotationView(annotation: annotation, reuseIdentifier: "myPin")
            
            pinAnnotationView.canShowCallout = true
            
            let routeInfoButton = CustomPinButton(type: UIButtonType.System)
            routeInfoButton.routeObjectID = pinAnnotation.parseObjectID
            routeInfoButton.addTarget(self, action: "pinPressed:", forControlEvents: .TouchUpInside)
            
            //TODO change image to something else
            if let image = UIImage(named: "menu.png") {
                routeInfoButton.setImage(image, forState: .Normal)
            }
            routeInfoButton.frame.size.width = 44
            routeInfoButton.frame.size.height = 44
            
            pinAnnotationView.leftCalloutAccessoryView = routeInfoButton
            
            return pinAnnotationView
        }
        
        return nil
    }
    
    
    //Executes when user touches an annotation's button if the button is of
    //type CustomPinButton
    func pinPressed(sender: CustomPinButton!) {
        self.routeObjectID = sender.routeObjectID
        performSegueWithIdentifier("routeInfo", sender: self)
    }
    
    
    /*
      Input: An address string as follows - # street, city, state, zip
             Any function that takes in a CLLocation and an error object and does some sort of non-returnable task
    
      Output: Nothing
      
      This function is used to call any type of function that needs to use a CLLocation instead of an address String. 
      This will allow the caller to do any sort of updating within the app specified by the second argument of the function.
      All in all, this function converts a String to a CLLocation.
     */
    func executeWithAddressString(address: String, getLocCompletionHandler : (convertedLocation : CLLocation?, error : NSError?) -> Void) {
        let geocoder = CLGeocoder()
        geocoder.geocodeAddressString(address, completionHandler: { (placemarks, error) -> Void in
            if error == nil {
                let placeArray = placemarks as [CLPlacemark]!
                
                // Place details
                var placeMark: CLPlacemark!
                placeMark = placeArray?[0]
                
                let location = CLLocation(
                    latitude: placeMark.location!.coordinate.latitude,
                    longitude: placeMark.location!.coordinate.longitude
                )
                
                //for testing
                /*
                for place in placeArray{
                    print(place.location!.coordinate.latitude)
                }*/

                getLocCompletionHandler(convertedLocation: location, error: error)
            }
        })
    }
    
    //Not used yet but is able to turn coordinate to an address string.
    //Will have to mimic execute with Address String later so that a user can
    //create a route through dropping a ping at a location
    func convertCoordsToAddress(locationInCoords: CLLocation){
        let geoCoder = CLGeocoder()
        geoCoder.reverseGeocodeLocation(locationInCoords, completionHandler: { (placemarks, error) -> Void in
            let placeArray = placemarks as [CLPlacemark]!
            
            // Place details
            var placeMark: CLPlacemark!
            placeMark = placeArray?[0]
            
            // Location name
            if let locationName = placeMark.addressDictionary?["Name"] as? NSString {
                print(locationName)
            }
            
            // City
            if let city = placeMark.addressDictionary?["City"] as? NSString {
                print(city)
            }
            
            // State
            if let state = placeMark.addressDictionary?["State"] as? NSString {
                print(state)
            }
            
            // Zip code
            if let zip = placeMark.addressDictionary?["ZIP"] as? NSString {
                print(zip)
            }
            
            // Country
            if let country = placeMark.addressDictionary?["Country"] as? NSString {
                print(country)
            }
            
        })

    }
    

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    //Let the RouteInfoView have the objectId of the ParseRoute selected
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject!) {
        // Create a new variable to store the instance of PlayerTableViewController
        let destinationVC = segue.destinationViewController as! RouteInfoViewController
        destinationVC.routeObjectID = self.routeObjectID
    }

}
