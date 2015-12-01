//
//  RouteInfoViewController.swift
//  ParseStarterProject-Swift
//
//  Created by Brian Wichers on 11/3/15.
//  Copyright © 2015 Parse. All rights reserved.
//

import UIKit
import Parse

class RouteInfoViewController: UIViewController {
    
    var routeObjectID: String?
    var driverObjectID: String?

    @IBOutlet weak var from: UITextField!
    @IBOutlet weak var to: UITextField!
    @IBOutlet weak var passengers: UITextField!
    
    //View Driver Account button
    @IBAction func driverAccount(sender: AnyObject) {
        performSegueWithIdentifier("viewDriverAccount", sender: self)
    }
    
    //Request to Join Carpool button
    @IBAction func joinCarpool(sender: AnyObject) {
        //passenger 
        
        let currentUserEmail = PFUser.currentUser()!.username
        let query = PFQuery(className: "ParseRoute")
        query.includeKey("objectId")
        query.whereKey("objectId", equalTo: self.routeObjectID!)
        query.findObjectsInBackgroundWithBlock { (objects, error) -> Void in
            if error == nil && objects != nil {
                let route = objects![0]
                
                let passRoute = route["passengers"] as? [String]
                if passRoute != nil {
                    if !passRoute!.contains(currentUserEmail!) {
                        self.signIn_alert("Already Joined",
                            alert_message: "You have already joined  this carpool route.")
                        return
                    }
                }
                
                var reqRoute = route["bookers"] as? [String]
                if reqRoute != nil {
                    if !reqRoute!.contains(currentUserEmail!){
                        reqRoute!.append(currentUserEmail!)
                        route["bookers"] = reqRoute
                        route.saveInBackground()
                        self.signIn_alert("Success",
                            alert_message: "You have successfully requested to join this carpool, please wait to be accepted by the driver.")
                    } else {
                        self.signIn_alert("Already requested",
                            alert_message: "You have already requested to join this carpool route, please wait to be accepted by the driver.")
                    }
                }
                
            }

        }
    }
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Do any additional setup after loading the view.
        let query = PFQuery(className: "ParseRoute")
        query.getObjectInBackgroundWithId(self.routeObjectID!) {
            (route: PFObject?, error: NSError?) -> Void in
            if error == nil && route != nil {
                self.from.text = route!["from"] as? String
                self.to.text = route!["to"] as? String
                self.passengers.text = route!["numPass"] as? String
                let driverObject = route!["user"] as? PFObject
                self.driverObjectID = driverObject?.objectId
            }
        }
    }
    
    func signIn_alert(alert_title: String, alert_message: String) {
        if #available(iOS 8.0, *) {
            let alertController = UIAlertController(title: alert_title, message: alert_message,
                preferredStyle: UIAlertControllerStyle.Alert)
            alertController.addAction(UIAlertAction(title: "Dismiss", style: UIAlertActionStyle.Default,handler: nil))
            self.presentViewController(alertController, animated: true, completion: nil)
        } else {
            // Fallback on earlier versions
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject!) {
        // Create a new variable to store the instance of PlayerTableViewController
        let destinationVC = segue.destinationViewController as! AccountViewController
        destinationVC.driverObjectID = self.driverObjectID
        destinationVC.routeObjectID = self.routeObjectID
    }

}
