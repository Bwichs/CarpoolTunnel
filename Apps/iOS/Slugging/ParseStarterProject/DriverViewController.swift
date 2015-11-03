//
//  DriverViewController.swift
//  ParseStarterProject-Swift
//
//  Created by Brian Wichers on 10/15/15.
//  Updated to table view and loading parse routes
//  Copyright © 2015 Parse. All rights reserved.
//

import UIKit
import ParseUI
import Parse


//class DriverViewController:UIViewController {
//    @IBOutlet weak var menuButton: UIBarButtonItem!
//    override func viewDidLoad() {
//                super.viewDidLoad()
//        
//                // Do any additional setup after loading the view.
//                if self.revealViewController() != nil {
//                    menuButton.target = self.revealViewController()
//                    menuButton.action = "revealToggle:"
//                    self.view.addGestureRecognizer(self.revealViewController().panGestureRecognizer())
//                }
//                self.revealViewController().rearViewRevealWidth = 160
//            }
//        
//            override func didReceiveMemoryWarning() {
//                super.didReceiveMemoryWarning()
//                // Dispose of any resources that can be recreated.
//            }
//
//}




class DriverViewController: PFQueryTableViewController {
    @IBOutlet var menuButton: UIBarButtonItem!
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Do any additional setup after loading the view.
        if self.revealViewController() != nil {
            menuButton.target = self.revealViewController()
            menuButton.action = "revealToggle:"
            self.view.addGestureRecognizer(self.revealViewController().panGestureRecognizer())
        }
        self.revealViewController().rearViewRevealWidth = 160
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    // Initialise the PFQueryTable tableview
    override init(style: UITableViewStyle, className: String!) {
        super.init(style: style, className: className)
    }
    
    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)!
        
        // Configure the PFQueryTableView
        self.parseClassName = "ParseRoute"
        self.textKey = "depDay"
        self.pullToRefreshEnabled = true
        self.paginationEnabled = true
        self.objectsPerPage = 5
    }
    
    // Define the query that will provide the data for the table view
    override func queryForTable() -> PFQuery {
        let current = PFUser.currentUser()?.objectId
        let query = PFQuery(className: "ParseRoute")
        query.orderByDescending("depDay")
        //query.orderByDescending("depDay").whereKey("user", equalTo: current!)
        return query
    }

    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath, object: PFObject?) -> PFTableViewCell {
        var cell = tableView.dequeueReusableCellWithIdentifier("cell") as! PFTableViewCell!
        if cell == nil {
            cell = PFTableViewCell(style: UITableViewCellStyle.Default, reuseIdentifier: "cell")
        }
        let from = object?["from"] as? String
        let date = object?["depDay"] as? String
        let passengers = object?["numPass"] as? String
        cell?.detailTextLabel?.text = "From :" + from! + " @ " + date! + " with " + passengers! + " passengers"
        
        if let to = object?["to"] as? String {
            cell?.textLabel?.text = "To: " + to
        }
        return cell
    }
    
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using [segue destinationViewController].
        let detailScene = segue.destinationViewController as! DriverViewTableDetail
        
        // Pass the selected object to the destination view controller.
        if let indexPath = self.tableView.indexPathForSelectedRow {
            let row = Int(indexPath.row)
            detailScene.currentObject = (objects?[row] as! PFObject)
        }
    }
    
    
    override func viewDidAppear(animated: Bool) {
        
        // Refresh the table to ensure any data changes are displayed
        tableView.reloadData()
    }
}

class DriverViewTableDetail: UIViewController {
    var currentObject : PFObject?
    
    @IBOutlet var from: UITextField!
    @IBOutlet var to: UITextField!
    @IBOutlet var numPass: UITextField!
    @IBOutlet var date: UIDatePicker!
    
    
    // The save button
    @IBAction func saveRoutes(sender: AnyObject) {
        
        // Unwrap the current object object
        if let object = currentObject {
            
            object["from"] = from.text
            object["to"] = to.text
            object["passengers"] = numPass.text
            // object["depDay"] = currencyCode.text
            
            // Save the data back to the server in a background task
            object.saveEventually(nil)
            
        }
        
        // Return to table view
        self.navigationController?.popViewControllerAnimated(true)
    }
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Unwrap the current object object
        if let object = currentObject {
            from.text = object["from"] as? String
            to.text = object["to"] as? String
            numPass.text = object["numPass"] as? String
            //date.textInputMode = object["depDay"] as? String
            //date.text = object["currencyCode"] as! String
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    

}

//class DriverViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {
//    @IBOutlet weak var menuButton: UIBarButtonItem!
//
//    @IBOutlet weak var tableView: UITableView!
//    
//    var textArray: NSMutableArray! = NSMutableArray()
//    
//    
//    override func viewDidLoad() {
//        super.viewDidLoad()
//
//        // Do any additional setup after loading the view.
//        if self.revealViewController() != nil {
//            menuButton.target = self.revealViewController()
//            menuButton.action = "revealToggle:"
//            self.view.addGestureRecognizer(self.revealViewController().panGestureRecognizer())
//        }
//        self.revealViewController().rearViewRevealWidth = 160
//        self.tableView.rowHeight = UITableViewAutomaticDimension
//        self.tableView.estimatedRowHeight = 44.0
//
//        
//    }
//
//    override func didReceiveMemoryWarning() {
//        super.didReceiveMemoryWarning()
//        // Dispose of any resources that can be recreated.
//    }
//    
//    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
//        return self.textArray.count
//    }
//    
//    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
//        let cell = tableView.dequeueReusableCellWithIdentifier("cell", forIndexPath: indexPath)
//
//        
//        cell.textLabel?.text = self.textArray.objectAtIndex(indexPath.row) as? String
//        
//        return cell;
//    }
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

//}
