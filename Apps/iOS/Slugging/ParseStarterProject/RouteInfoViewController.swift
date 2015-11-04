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
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Do any additional setup after loading the view.
        let query = PFQuery(className: "ParseRoute")
        query.getObjectInBackgroundWithId(self.parseObjectID!) {
            (user: PFObject?, error: NSError?) -> Void in
            if error == nil && user != nil {
                self.from.text = user!["from"] as? String
                self.to.text = user!["to"] as? String
                self.passengers.text = user!["numPass"] as? String
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
