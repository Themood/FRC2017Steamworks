import cv2
import numpy as np
import math

class Boiler:
    """
    An OpenCV pipeline generated by GRIP.
    """
    
    def __init__(self):
        """initializes all values to presets or None if need to be set
        """
        self.MIN_MATCH_COUNT = 10

        # load the image, convert it to grayscale, and detect edges
        self.trainingImage = cv2.imread("redBoilerTrainWhole.jpg",cv2.IMREAD_GRAYSCALE)
        print(self.trainingImage.shape)        

        # Initiate a detector
        #self.detector = cv2.xfeatures2d.SIFT_create()
        self.detector = cv2.xfeatures2d.SURF_create()
        self.norm = cv2.NORM_L2       
        # find the keypoints and descriptors with SIFT
        self.kp1, self.des1 = self.detector.detectAndCompute(self.trainingImage,None)

        # create BFMatcher object
        self.crossCheck = False
        self.bf = cv2.BFMatcher(self.norm, self.crossCheck)


    def process(self, source0):
        """
        Runs the pipeline and sets all outputs to new values.
        """
        
        img2 = cv2.cvtColor(source0, cv2.COLOR_BGR2GRAY)
        kp2, des2 = self.detector.detectAndCompute(img2,None)

        # Match descriptors.
        if (self.crossCheck == True):
            matches = self.bf.match(self.des1,des2)
            # Sort them in the order of their distance.
            matches = sorted(matches, key = lambda x:x.distance)
        else:
            if (des2 == None):
                matches = None
            else:
                matches = self.bf.knnMatch(self.des1,des2,k=2)
            
        if ((self.crossCheck == False) and (matches != None) and (len(matches) > 1)):
            # store all the good matches as per Lowe's ratio test.
            good = []
            for m,n in matches:
                if m.distance < 0.7*n.distance:
                    good.append(m)
            
            goodCount= len(good)
            if (goodCount>self.MIN_MATCH_COUNT):
                src_pts = np.float32([ self.kp1[m.queryIdx].pt for m in good ]).reshape(-1,1,2)
                dst_pts = np.float32([ kp2[m.trainIdx].pt for m in good ]).reshape(-1,1,2)
                M, mask = cv2.findHomography(src_pts, dst_pts, cv2.RANSAC,5.0)
                if (mask != None):
                    matchesMask = mask.ravel().tolist()
                    h,w = self.trainingImage.shape
                    pts = np.float32([ [0,0],[0,h-1],[w-1,h-1],[w-1,0] ]).reshape(-1,1,2)
                    dst = cv2.perspectiveTransform(pts,M)
                    
                    
                    angle = (goodCount-self.MIN_MATCH_COUNT-1)
                    if (angle > 50):
                        angle = 50
                    
                    angle = math.pi/2 * (angle / 50)
                    r = int(math.cos(angle)*255)
                    g = int(math.sin(angle)*255)
                    
                    cv2.polylines(source0,[np.int32(dst)],True,(0,g,r),2, cv2.LINE_AA)
                
                # The above polygon should be a quadralateral and should
                # represent the extent of the boiler (even beyond the image)
                # With some calibration we should be able to estimate the
                # distance and angle, as well as estimate where the high
                # goal should be.
                
            else:
                
                #print("Not enough matches are found - %d/%d" % (len(good),self.MIN_MATCH_COUNT))
                matchesMask = None
            
            
#            draw_params = dict(matchColor = (0,255,0), # draw matches in green color
#                               singlePointColor = None,
#                               matchesMask = matchesMask, # draw only inliers
#                               flags = 2)
            
            #img3 = cv2.drawMatches(self.trainingImage,self.kp1,img2,kp2,good,None,**draw_params)
                
#        else:
#            ## Draw first 10 matches.
#            img3 = cv2.drawMatches(self.trainingImage,self.kp1,img2,kp2,matches[:10], None, flags=2)        