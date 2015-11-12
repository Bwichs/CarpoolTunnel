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

class PinButton: UIButton {
    var parseObjectID: String?
}

@available(iOS 8.0, *)
class PassengerViewController: UIViewController, MKMapViewDelegate, CLLocationManagerDelegate {
    var parseObjectID: String?
    
    @IBOutlet weak var menuButton: UIBarButtonItem!
    
    @IBOutlet weak var mapView: MKMapView!
    
    var locationManager = CLLocationManager()
    
    func centerMapOnLocation(location: CLLocation) {
        let regionRadius: CLLocationDistance = 1000
        let coordinateRegion = MKCoordinateRegionMakeWithDistance(location.coordinate,
            regionRadius * 2.0, regionRadius * 2.0)
        mapView.setRegion(coordinateRegion, animated: true)
    }
    
    func addPin(location: CLLocation, pinLabel: String, objectID: String){
        let dropPin = PinAnnotation(pinCoord: location.coordinate, objectID: objectID)
        dropPin.title = pinLabel
        mapView.addAnnotation(dropPin)
    }
   
    //Click pins to join ride
    func mapView(mapView: MKMapView, viewForAnnotation annotation: MKAnnotation) -> MKAnnotationView? {
        if annotation is PinAnnotation {
            let pinAnnotation = annotation as! PinAnnotation
            let pinAnnotationView = MKPinAnnotationView(annotation: annotation, reuseIdentifier: "myPin")
            
            pinAnnotationView.canShowCallout = true
            
            let routeInfoButton = PinButton(type: UIButtonType.System)
            routeInfoButton.parseObjectID = pinAnnotation.parseObjectID
            routeInfoButton.addTarget(self, action: "pinPressed:", forControlEvents: .TouchUpInside)
            
            
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
    func pinPressed(sender: PinButton!) {
        self.parseObjectID = sender.parseObjectID
        performSegueWithIdentifier("routeInfo", sender: self)
    }
    
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject!) {
        // Create a new variable to store the instance of PlayerTableViewController
        let destinationVC = segue.destinationViewController as! RouteInfoViewController
        destinationVC.parseObjectID = self.parseObjectID
    }
    
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
                        let objectID = object.objectId! as String
                        self.executeWithAddressString(fromAddress)
                            { convertedLocation, error in
                                self.addPin(convertedLocation!, pinLabel: toAddress, objectID: objectID)
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
    
    func locationManager(manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        let userLocation:CLLocation = locations[0]
        centerMapOnLocation(userLocation)
        locationManager.stopUpdatingLocation()
    }
    
    func locationManager(manager: CLLocationManager, didFailWithError error: NSError) {
        print("Error while updating location " + error.localizedDescription)
    }


    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
