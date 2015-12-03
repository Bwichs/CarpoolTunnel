//
//  MyRoutesViewController.swift
//  ParseStarterProject-Swift
//
//  Created by Brian Wichers on 12/1/15.
//  Copyright © 2015 Parse. All rights reserved.
//

import UIKit
import Parse
import ParseUI

class MyRoutesViewController: PFQueryTableViewController {
    
    @IBOutlet weak var menuButton: UIBarButtonItem!
    
    
    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)!
        
        // Configure the PFQueryTableView
        self.parseClassName = "ParseRoute"
        
        self.textKey = "to"
        self.pullToRefreshEnabled = true
        self.paginationEnabled = false
    }
    
    
    override func queryForTable() -> PFQuery {
        let currentUserEmail = PFUser.currentUser()!.username
        let query = PFQuery(className: "ParseRoute")
        query.whereKey("passengers", containsAllObjectsInArray: [currentUserEmail!])
        return query
    }
    
    
    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath, object: PFObject?) -> PFTableViewCell {
        self.tableView.separatorStyle = UITableViewCellSeparatorStyle.None
        var cell = tableView.dequeueReusableCellWithIdentifier("cell") as! PFTableViewCell!
        if cell == nil {
            cell = PFTableViewCell(style: UITableViewCellStyle.Default, reuseIdentifier: "cell")
        }
        cell.textLabel?.font = UIFont.systemFontOfSize(24)
        cell.detailTextLabel?.font = UIFont.systemFontOfSize(18)
        
        let dateFormatter = NSDateFormatter()
        dateFormatter.dateFormat = "HH:mm"
        let deptime = object?["depTime"] as? String
        let timedate = dateFormatter.dateFromString(deptime!)
        dateFormatter.dateFormat = "h:mm a"
        let time = dateFormatter.stringFromDate(timedate!)
        
        cell?.detailTextLabel?.text = "Depart time: " + time
        if let to = object?["to"] as? String {
            cell?.textLabel?.text = "To: " + to
        }
        return cell
    }
    
    
    
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
    
    override func viewDidAppear(animated: Bool) {
        
        // Refresh the table to ensure any data changes are displayed
        tableView.reloadData()
    }
}
