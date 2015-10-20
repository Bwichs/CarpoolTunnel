/**
* Copyright (c) 2015-present, Parse, LLC.
* All rights reserved.
*
* This source code is licensed under the BSD-style license found in the
* LICENSE file in the root directory of this source tree. An additional grant
* of patent rights can be found in the PATENTS file in the same directory.
*/

import UIKit
import Parse

class LogginViewController: UIViewController {
    
    
    @IBOutlet weak var emailAddress: UITextField!
    @IBOutlet weak var password: UITextField!
    @IBOutlet weak var activityIndicator: UIActivityIndicatorView!
    
    @IBAction func register_or_signIn(sender: AnyObject) {
        var userEmailAddress = emailAddress.text
        let userPassword = password.text
        
        // Ensure username is lowercase
        userEmailAddress = userEmailAddress!.lowercaseString
        // Ensure email is @ucsc.edu
        
        
        // Start activity indicator
        activityIndicator.hidden = false
        activityIndicator.startAnimating()
        
        // Create the user
        let user = PFUser()
        user.username = userEmailAddress
        user.password = userPassword
        user.email = userEmailAddress
        
        PFUser.logInWithUsernameInBackground(userEmailAddress!, password: (userPassword)!) {
            (return_user: PFUser?, error: NSError?) -> Void in
            let is_UCSC = self.check_ucsc_email(userEmailAddress!)
            if !is_UCSC {
                self.signIn_alert("Email address",
                    alert_message: "Please enter a valid @ucsc.edu email address to use this app.")
                
                // // Sign out
                PFUser.logOut()                
            }
            else {
                if return_user != nil {
                    //Verify email
                    if return_user?["emailVerified"] as! Bool == true {
                        self.activityIndicator.stopAnimating()
                        dispatch_async(dispatch_get_main_queue()) {
                            self.performSegueWithIdentifier(
                                "go_to_main_menu",
                                sender: self)
                        }
                    }
                    else {
                        self.signIn_alert("Email address verification",
                            alert_message: "We have sent you an email that contains a link - you must click this link before you can continue.")
                    }
                } else {
                    // The login failed. Check error to see why. Either incorrect password, or does not exist yet
                    self.try_signUp(user)
                }
            }
        }
    }
    
    func check_ucsc_email(userEmailAddress: String) -> Bool{
        if userEmailAddress.rangeOfString("@ucsc.edu") != nil{
            return true
        }
        else {
            return false
        }
    }
    
    func try_signUp(user: PFUser) {
        user.signUpInBackgroundWithBlock {
            (succeeded: Bool, error: NSError?) -> Void in
            if let error = error {
                _ = error.userInfo["error"] as? NSString
                // User is already registered, password was incorrect
                self.signIn_alert("Password incorrect",
                                  alert_message: "Email in use, please enter correct password.")
            } else {
                // Account registered, please verify email
                self.signIn_alert("Email address verification",
                                  alert_message: "We have sent you an email that contains a link - you must click this link before you can continue.")
            }
        }

    }

    func signIn_alert(alert_title: String, alert_message: String) {
        self.activityIndicator.stopAnimating()
        if #available(iOS 8.0, *) {
            let alertController = UIAlertController(title: alert_title, message: alert_message,
                                                    preferredStyle: UIAlertControllerStyle.Alert)
            alertController.addAction(UIAlertAction(title: "Dismiss", style: UIAlertActionStyle.Default,handler: nil))
            self.presentViewController(alertController, animated: true, completion: nil)
        } else {
            // Fallback on earlier versions
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
    }
    
    override func viewDidAppear(animated: Bool) {
        let currentUser = PFUser.currentUser()
        if currentUser != nil {
            // Do stuff with the user
        
        self.performSegueWithIdentifier("go_to_main_menu", sender: self)
            
        } else {
            // Show the signup or login screen
        }

    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
}
