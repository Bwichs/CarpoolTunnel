//
//  MapViewController.swift
//  
//
//  Created by Brian Wichers on 10/13/15.
//
//

import UIKit
import Parse

class MapViewController: UIViewController {

    @IBAction func logOut(sender: AnyObject) {
        PFUser.logOut();
        self.performSegueWithIdentifier("backToSignIn", sender: self)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
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
