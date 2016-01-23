//
//  DriverViewController.swift
//  ParseStarterProject-Swift
//
//  Created by Brian Wichers on 10/15/15.
//  Kevin Jesse - Added table view and detail view
//  Kevin Jesse - Made table view editable and reloadable
//  Kevin Jesse - Added + with new route button
//  Kevin Jesse - Fixed datepicker and made UI changes from TA (pagination)
//  Kevin Jesse - Added Delete functionality
//  Copyright © 2015 Parse. All rights reserved.
//

import UIKit
import ParseUI
import Parse

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
        self.paginationEnabled = false
       
    }
    
    // Define the query that will provide the data for the table view
    override func queryForTable() -> PFQuery {
        let current = PFUser.currentUser()
        let query = PFQuery(className: "ParseRoute")
        query.orderByDescending("depDay").whereKey("user", equalTo: current!)
        return query
    }

    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath, object: PFObject?) -> PFTableViewCell {
        self.tableView.separatorStyle = UITableViewCellSeparatorStyle.None
        var cell = tableView.dequeueReusableCellWithIdentifier("cell") as! PFTableViewCell!
        if cell == nil {
            cell = PFTableViewCell(style: UITableViewCellStyle.Default, reuseIdentifier: "cell")
        }
        //cell.textLabel?.adjustsFontSizeToFitWidth = true
        cell.textLabel?.font = UIFont.systemFontOfSize(24)
        cell.detailTextLabel?.font = UIFont.systemFontOfSize(18)
        let from = object?["from"] as? String
        let date = object?["depDay"] as? String
        let deptime = object?["depTime"] as? String
        let dateFormatter = NSDateFormatter()
        dateFormatter.dateFormat = "HH:mm"
        let timedate = dateFormatter.dateFromString(deptime!)
        dateFormatter.dateFormat = "h:mm a"
        let time = dateFormatter.stringFromDate(timedate!)
        
        let passengers = object?["numPass"] as? String
        cell?.detailTextLabel?.text = "From :" + from! + " @ " + date! + " with " + passengers! + " passengers"
        
        if let to = object?["to"] as? String {
            cell?.textLabel?.text = "To: " + to + " - " + time
        }
        return cell
    }
    
    override func tableView(tableView: UITableView, commitEditingStyle editingStyle: UITableViewCellEditingStyle, forRowAtIndexPath indexPath: NSIndexPath){
        if editingStyle == UITableViewCellEditingStyle.Delete {
            self.removeObjectAtIndexPath(indexPath)
        }
    }
    
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        let detailScene = segue.destinationViewController as! DriverViewTableDetail
        detailScene.navigationItem.title = "Your Route"
        navigationItem.title = "Routes"
        
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
    
    @IBAction func add(sender: AnyObject) {
        dispatch_async(dispatch_get_main_queue()) {
            self.performSegueWithIdentifier("DriverViewTableDetail", sender: self)
        }
    }
    
    
}

class DriverViewTableDetail: UIViewController, UITextFieldDelegate {
    var currentObject : PFObject?
    
    @IBOutlet var from: UITextField!
    @IBOutlet var to: UITextField!
    @IBOutlet var numPass: UITextField!
    @IBOutlet weak var date: UIDatePicker!

    // The save button
    @IBAction func saveRoute(sender: AnyObject) {
        
        let dateFormatter = NSDateFormatter()
        //dateFormatter.dateFormat = "MM/dd/yyyy"
        let strDate = dateFormatter.stringFromDate(date.date)
        dateFormatter.dateFormat = "HH:mm"
        let strTime = dateFormatter.stringFromDate(date.date)
        
        // Unwrap the current object object
        if let object = currentObject as PFObject? {
            
            //update the existing object
            object["from"] = from.text
            object["to"] = to.text
            object["numPass"] = numPass.text
            object["depDay"] = strDate
            object["depTime"] = strTime
            
            // Save the data back to the server in a background task
            object.saveEventually()
            
        }else {
            //create the new object
            let object = PFObject(className: "ParseRoute")
            
            //update the existing object
            object["from"] = from.text
            object["to"] = to.text
            object["numPass"] = numPass.text
            object["depDay"] = strDate
            object["depTime"] = strTime
            object["user"] = PFUser.currentUser()
            
            // Save the data back to the server in a background task
            object.saveEventually()
            
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
        }
        self.from.delegate = self
        self.to.delegate = self
        self.numPass.delegate = self
        
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: "dismissKeyboard")
        view.addGestureRecognizer(tap)
        
        
    }
    
    
    func textFieldShouldReturn(textField: UITextField) -> Bool {
        self.view.endEditing(true)
        return false
    }
    
    //Calls this function when the tap is recognized.
    func dismissKeyboard() {
        //Causes the view (or one of its embedded text fields) to resign the first responder status.
        view.endEditing(true)
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
}

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

//}
