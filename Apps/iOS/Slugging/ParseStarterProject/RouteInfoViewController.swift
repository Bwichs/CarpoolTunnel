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
    
    var parseObjectID: String?

    @IBOutlet weak var from: UITextField!
    @IBOutlet weak var to: UITextField!
    @IBOutlet weak var passengers: UITextField!
    
    @IBAction func joinCarpool(sender: AnyObject) {
        //passenger 
        
        let currentUserID = PFUser.currentUser()?.objectId
        let query = PFQuery(className: "ParseRoute")
        query.includeKey("objectId")
        query.whereKey("objectId", equalTo: self.parseObjectID!)
        query.findObjectsInBackgroundWithBlock { (objects, error) -> Void in
            if error == nil {
                for route in objects! {
                    let userPointer:PFObject = (route["user"] as? PFObject)!
                    let userQuery = PFUser.query()
                    userQuery!.getObjectInBackgroundWithId(userPointer.objectId!){
                        (driver: PFObject?, error: NSError?) -> Void in
                        if error == nil && driver != nil {
                            var reqArray = driver!["reqPassengers"] as? [String]
                            if reqArray == nil {
                                reqArray = []
                            }
                            reqArray!.append(currentUserID!)
                            driver!["reqPassengers"] = reqArray
                            driver!.saveInBackgroundWithBlock {
                                (success, error) -> Void in
                            }
                        }
                    }
                }
            }
                
        /*
        userPointer["objectId"] as? String
        let userPointer:PFObject = (route["user"] as? PFObject)!
        var reqArray = driver!["reqPassengers"] as? [String]
        reqArray!.append(currentUserID!)
        route!["user"] = reqArray
        route!.saveInBackgroundWithBlock {
        */

        }
    }
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Do any additional setup after loading the view.
        let query = PFQuery(className: "ParseRoute")
        query.getObjectInBackgroundWithId(self.parseObjectID!) {
            (route: PFObject?, error: NSError?) -> Void in
            if error == nil && route != nil {
                self.from.text = route!["from"] as? String
                self.to.text = route!["to"] as? String
                self.passengers.text = route!["numPass"] as? String
            }
        }
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
